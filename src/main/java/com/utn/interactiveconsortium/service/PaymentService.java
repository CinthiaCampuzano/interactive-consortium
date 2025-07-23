package com.utn.interactiveconsortium.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.utn.interactiveconsortium.config.MinioConfig;
import com.utn.interactiveconsortium.dto.PaymentDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.entity.DepartmentFeeEntity;
import com.utn.interactiveconsortium.entity.PaymentEntity;
import com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus;
import com.utn.interactiveconsortium.enums.EPaymentMethod;
import com.utn.interactiveconsortium.enums.EPaymentStatus;
import com.utn.interactiveconsortium.exception.CustomGenericException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.PaymentMapper;
import com.utn.interactiveconsortium.repository.ConsortiumFeePeriodRepository;
import com.utn.interactiveconsortium.repository.DepartmentFeeRepository;
import com.utn.interactiveconsortium.repository.PaymentRepository;
import com.utn.interactiveconsortium.util.EmailService;
import com.utn.interactiveconsortium.util.MinioUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

   private final LoggedUserService loggedUserService;

   private final PaymentRepository paymentRepository;

   private final DepartmentFeeRepository departmentFeeRepository;

   private final ConsortiumFeePeriodRepository consortiumFeePeriodRepository;

   private final MinioConfig minioConfig;

   private final MinioUtils minioUtils;

   private final EmailService emailService;

   private final PaymentMapper paymentMapper;

   private static final String FILE_SEPARATOR = "/";

   private static final String PAYMENTS_BASE_FOLDER = "PAGOS";

   private static final String MAINTENANCE_FEE_PAYMENT_SUBJECT = "Recibo de expensas del mes de %s - Consoricio %s";

   private static final List<EConsortiumFeePeriodStatus> CONSORTIUM_FEE_PERIOD_STATUSES_ALLOWED_TO_BE_PAID = List.of(
         EConsortiumFeePeriodStatus.GENERATED,
         EConsortiumFeePeriodStatus.SENT
   );

   @Transactional(rollbackOn = Exception.class)
   public PaymentDto createPayment(PaymentDto paymentDto, MultipartFile file)
         throws EntityNotFoundException, MessagingException, IOException, CustomGenericException {

      List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
      if (!associatedConsortiumIds.contains(paymentDto.getDepartmentFee().getConsortiumFeePeriod().getConsortium().getConsortiumId())) {
         throw new EntityNotFoundException("No se encontro el consorcio");
      }

      DepartmentFeeEntity departmentFee = departmentFeeRepository
            .findById(paymentDto.getDepartmentFee().getDepartmentFeeId())
            .orElseThrow(() -> new EntityNotFoundException("No se encontro la deuda del departamento para el periodo"));

      ConsortiumFeePeriodEntity consortiumFeePeriod = departmentFee.getConsortiumFeePeriod();
      if (!CONSORTIUM_FEE_PERIOD_STATUSES_ALLOWED_TO_BE_PAID.contains(consortiumFeePeriod.getFeePeriodStatus())) {
         throw new CustomGenericException("Solo se pueden pagar expensas en estado GENERADO o ENVIADO");
      }

      BigDecimal pendingAmount = departmentFee.getTotalAmount().subtract(departmentFee.getPaidAmount());
      BigDecimal newAmountPaid = paymentDto.getAmount();
      boolean isPaidCompletely = pendingAmount.subtract(newAmountPaid).compareTo(BigDecimal.ZERO) > 0;

      departmentFee.setPaymentStatus(isPaidCompletely ? EPaymentStatus.PAID : EPaymentStatus.PARTIALLY_PAID);
      departmentFee.setPaidAmount(departmentFee.getPaidAmount().add(newAmountPaid));

      PaymentEntity payment = PaymentEntity
            .builder()
            .paymentMethod(EPaymentMethod.CASH)
            .paymentDate(LocalDate.now())
            .departmentFee(departmentFee)
            .amount(newAmountPaid)
            .build();

      paymentRepository.save(payment);
      departmentFee.getPayments().add(payment);

      LocalDate period = consortiumFeePeriod.getPeriodDate();
      DepartmentEntity department = departmentFee.getDepartment();
      ConsortiumEntity consortium = consortiumFeePeriod.getConsortium();

      String fileName = generateFileName(consortium, department, period,
            file);
      payment.setReceiptFileName(fileName);
      PaymentEntity paymentSaved = paymentRepository.save(payment);

      String filePath = getFilePathFor(consortium, department, period,
            fileName);

      minioUtils.uploadFile(minioConfig.getBucketName(), file, filePath, file.getContentType());
      InputStream inputStream = minioUtils.getObject(minioConfig.getBucketName(), filePath);

      Set<String> mails = new HashSet<>();
      mails.add(department.getResident().getMail());
      mails.add(department.getPropietary().getMail());
      sendMaintenanceFeeMailWithAttachment(mails, consortium.getName(), period, fileName, inputStream);

      return paymentMapper.convertEntityToDto(paymentSaved);
   }

       private String generateFileName(ConsortiumEntity consortium, DepartmentEntity department, LocalDate period, MultipartFile file) {
           return "Recibo" +
                   consortium.getName().replace(' ', '_') +
                   "_" +
                   department.getCode() +
                   "_" +
                   period.getYear() +
                   "_" +
                   period.getMonthValue() +
                   getFileExtension(file);
       }

       private String getFileExtension(MultipartFile file) {
           String originalFileName = file.getOriginalFilename();
           if (originalFileName != null && originalFileName.contains(".")) {
               return originalFileName.substring(originalFileName.lastIndexOf("."));
           }
           return "";
       }

       private String getFilePathFor(ConsortiumEntity consortium, DepartmentEntity department, LocalDate period,
                                     String fileName) {
           return PAYMENTS_BASE_FOLDER +
                 FILE_SEPARATOR +
                 consortium.getConsortiumId() +
                 FILE_SEPARATOR +
                 period.getYear() +
                 FILE_SEPARATOR +
                 period.getMonthValue() +
                 FILE_SEPARATOR +
                 department.getDepartmentId() +
                 FILE_SEPARATOR +
                 fileName;
       }

       public void sendMaintenanceFeeMailWithAttachment(Set<String> mails, String consortiumName, LocalDate period, String fileName, InputStream file) throws MessagingException, IOException {
           String subject = String.format(MAINTENANCE_FEE_PAYMENT_SUBJECT, getMonthName(period) + " " + period.getYear(), consortiumName);
           emailService.sendMessageWithAttachment(mails.toArray(new String[0]), subject, null, fileName, file);
       }

       private String getMonthName(LocalDate period) {
           switch (period.getMonthValue()) {
               case 1:
                   return "Enero";
               case 2:
                   return "Febrero";
               case 3:
                   return "Marzo";
               case 4:
                   return "Abril";
               case 5:
                   return "Mayo";
               case 6:
                   return "Junio";
               case 7:
                   return "Julio";
               case 8:
                   return "Agosto";
               case 9:
                   return "Septiembre";
               case 10:
                   return "Octubre";
               case 11:
                   return "Noviembre";
               case 12:
                   return "Diciembre";
               default:
                   return "";
           }
       }
}
