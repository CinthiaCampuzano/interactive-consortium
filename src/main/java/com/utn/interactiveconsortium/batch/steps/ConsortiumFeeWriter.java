package com.utn.interactiveconsortium.batch.steps;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.mail.MessagingException;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.utn.interactiveconsortium.batch.wrapper.ConsortiumFeeWrapper;
import com.utn.interactiveconsortium.batch.wrapper.JasperWrapper;
import com.utn.interactiveconsortium.config.MinioConfig;
import com.utn.interactiveconsortium.entity.AdjustmentEntity;
import com.utn.interactiveconsortium.entity.BookingEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodItemEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.entity.DepartmentFeeEntity;
import com.utn.interactiveconsortium.entity.DepartmentFeeItemEntity;
import com.utn.interactiveconsortium.entity.PersonEntity;
import com.utn.interactiveconsortium.enums.EConsortiumFeeConceptType;
import com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus;
import com.utn.interactiveconsortium.enums.EOperationType;
import com.utn.interactiveconsortium.enums.EPaymentStatus;
import com.utn.interactiveconsortium.enums.EShift;
import com.utn.interactiveconsortium.repository.ConsortiumFeePeriodRepository;
import com.utn.interactiveconsortium.repository.DepartmentFeeRepository;
import com.utn.interactiveconsortium.repository.DepartmentRepository;
import com.utn.interactiveconsortium.service.AdjustmentService;
import com.utn.interactiveconsortium.service.BookingService;
import com.utn.interactiveconsortium.service.ConsortiumFeePeriodService;
import com.utn.interactiveconsortium.service.PaymentService;
import com.utn.interactiveconsortium.service.PdfGenerationService;
import com.utn.interactiveconsortium.util.EmailService;
import com.utn.interactiveconsortium.util.MinioUtils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsortiumFeeWriter implements ItemWriter<ConsortiumFeeWrapper>, ItemWriteListener<ConsortiumFeeWrapper> {

   private final ConsortiumFeePeriodRepository consortiumFeePeriodRepository;

   private final DepartmentFeeRepository departmentFeeRepository;

   private final BookingService bookingService;
   
   private final AdjustmentService adjustmentService;

   private final PdfGenerationService pdfGenerationService;

   private final PaymentService paymentService;

   private final EmailService emailService;

   private final MinioUtils minioUtils;

   private final MinioConfig minioConfig;

   private final DepartmentRepository departmentRepository;

   private final ConsortiumFeePeriodService consortiumFeePeriodService;

   //TODO falta control para no generar dos veces y se necesita otro solo para regenerar
   @Override
   public void write(Chunk<? extends ConsortiumFeeWrapper> chunk) throws Exception {
      for(ConsortiumFeeWrapper wrapper : chunk.getItems()) {
         ConsortiumFeePeriodEntity consortiumFeePeriod = wrapper.getConsortiumFeePeriod();
         List<ConsortiumFeePeriodItemEntity> periodItems = wrapper.getPeriodConcepts();
         consortiumFeePeriod.setFeePeriodItems(periodItems);

         // Persistir la entidad con todos sus datos y la ruta del PDF
         consortiumFeePeriod = consortiumFeePeriodRepository.save(consortiumFeePeriod);
         log.debug("Period saved successfully: {}", consortiumFeePeriod.getConsortiumFeePeriodId());


         // La lógica para generar DepartmentFee y DepartmentFeeItem
         List<DepartmentEntity> activeDepartments = consortiumFeePeriod
               .getConsortium()
               .getDepartments()
               .stream()
               .filter(DepartmentEntity::getActive)
               .toList();

         LocalDate issueDate = consortiumFeePeriod.getGenerationDate();
         LocalDate dueDate = consortiumFeePeriod.getDueDate();

         int numActiveDepartments = activeDepartments.size();
         List<DepartmentFeeEntity> departmentFeeOfPeriod = new ArrayList<>(numActiveDepartments);
         for (DepartmentEntity department : activeDepartments) {
            List<DepartmentFeeItemEntity> departmentFeeItems = new ArrayList<>();
            DepartmentFeeEntity departmentFee = DepartmentFeeEntity
                  .builder()
                  .consortiumFeePeriod(consortiumFeePeriod)
                  .department(department)
                  .totalAmount(BigDecimal.ZERO)
                  .dueAmount(BigDecimal.ZERO)
                  .paidAmount(BigDecimal.ZERO)
                  .paymentStatus(EPaymentStatus.PENDING)
                  .issueDate(issueDate)
                  .dueDate(dueDate)
                  .build();

            BigDecimal departmentAmount = BigDecimal.ZERO;
            boolean adjustmentsCalculated = false;
            for (ConsortiumFeePeriodItemEntity item : consortiumFeePeriod.getFeePeriodItems()) {
               //TODO Agregar logica para agregar items de reservas y ajustes
               if (item.getConceptType() == EConsortiumFeeConceptType.ADJUSTMENT && adjustmentsCalculated) {
                  continue;
               }

               List<DepartmentFeeItemEntity> departmentFeeItemsForConsortiumItem = generateDepartmentFeeItemsFor(item, department, activeDepartments,
                     departmentFee);
               departmentFeeItems.addAll(departmentFeeItemsForConsortiumItem);

               BigDecimal totalAmountForConsortiumItem = departmentFeeItemsForConsortiumItem
                     .stream()
                     .map(DepartmentFeeItemEntity::getAmount)
                     .reduce(BigDecimal.ZERO, BigDecimal::add);

               departmentAmount = departmentAmount.add(totalAmountForConsortiumItem);
               if (item.getConceptType() == EConsortiumFeeConceptType.ADJUSTMENT) {
                  adjustmentsCalculated = true;
               }
            }

            //todo sacar de la parte de arriba y merlo aca abajo

            departmentFee.setDepartmentFeeItems(departmentFeeItems);
            departmentFee.setTotalAmount(departmentAmount);
            departmentFeeOfPeriod.add(departmentFee);
         }

         departmentFeeRepository.saveAll(departmentFeeOfPeriod);

         // Preparar datos para el reporte Jasper
         JasperWrapper jasperReportData = new JasperWrapper(
               consortiumFeePeriod,
               consortiumFeePeriod.getFeePeriodItems(), // Esto es wrapper.getPeriodConcepts()
               departmentFeeOfPeriod // La lista de DepartmentFeeEntity que ya calculaste
         );

         // Generar el PDF general del consorcio
         byte[] consortiumPdfBytes = pdfGenerationService.generateConsortiumFeePdf(jasperReportData);

         // Subir a MinIO
         String consortiumFilePath = generateConsortiumPdfPath(consortiumFeePeriod);
         minioUtils.uploadFile(
               minioConfig.getBucketName(),
               consortiumFilePath,
               new ByteArrayInputStream(consortiumPdfBytes)
         );
         log.info("PDF for consortium {} uploaded to MinIO at: {}", consortiumFeePeriod.getConsortium().getConsortiumId(), consortiumFilePath);

         // Guardar la ruta en la entidad
         consortiumFeePeriod.setPdfFilePath(consortiumFilePath);
         consortiumFeePeriodRepository.save(consortiumFeePeriod);

         // Generar PDFs individuales para cada departamento
         Map<String, List<Pair<String, byte[]>>> emailAttachments = new HashMap<>();
         Map<String, String> emailRecipientNames = new HashMap<>();
         
         for (DepartmentFeeEntity departmentFee : departmentFeeOfPeriod) {
            // Generar PDF para el departamento
            byte[] departmentPdfBytes = pdfGenerationService.generateDepartmentFeePdf(departmentFee);
            
            // Subir a MinIO
            String departmentFilePath = generateDepartmentPdfPath(departmentFee);
            ByteArrayInputStream departmentPdfStream = new ByteArrayInputStream(departmentPdfBytes);
            minioUtils.uploadFile(
                  minioConfig.getBucketName(),
                  departmentFilePath,
                  departmentPdfStream
            );
            departmentFee.setPdfFilePath(departmentFilePath);
            log.info("PDF for department {} uploaded to MinIO at: {}", 
                  departmentFee.getDepartment().getCode(), departmentFilePath);

            // Extraer el nombre del archivo de la ruta para usarlo en el correo
            String filename = departmentFilePath.substring(departmentFilePath.lastIndexOf('/') + 1);

            // Recolectar destinatarios de correo
            collectEmailRecipients(departmentFee, emailAttachments, emailRecipientNames, departmentPdfBytes, filename);
         }
         departmentFeeRepository.saveAll(departmentFeeOfPeriod);
         // Enviar correos electrónicos
         sendEmails(consortiumFeePeriod, emailAttachments, emailRecipientNames, consortiumPdfBytes);
      }
   }

   private void collectEmailRecipients(
         DepartmentFeeEntity departmentFee,
         Map<String, List<Pair<String, byte[]>>> emailAttachments,
         Map<String, String> emailRecipientNames,
         byte[] departmentPdfBytes,
         String attachmentFilename
   ) {
      DepartmentEntity department = departmentFee.getDepartment();
      PersonEntity propietary = department.getPropietary();
      PersonEntity resident = department.getResident();

      Pair<String, byte[]> attachment = Pair.of(attachmentFilename, departmentPdfBytes);

      // Agregar propietario
      if (propietary != null && propietary.getMail() != null && !propietary.getMail().isEmpty()) {
         String email = propietary.getMail();
         emailRecipientNames.put(email, propietary.getName() + " " + propietary.getLastName());
         emailAttachments.computeIfAbsent(email, k -> new ArrayList<>())
                        .add(attachment);
      }
      
      // Agregar residente (si es diferente del propietario)
      if (resident != null && resident.getMail() != null && !resident.getMail().isEmpty() && 
          (propietary == null || !resident.getMail().equals(propietary.getMail()))) {
         String email = resident.getMail();
         emailRecipientNames.put(email, resident.getName() + " " + resident.getLastName());
         emailAttachments.computeIfAbsent(email, k -> new ArrayList<>())
                        .add(attachment);
      }
   }
   
   private void sendEmails(ConsortiumFeePeriodEntity consortiumFeePeriod,
                          Map<String, List<Pair<String, byte[]>>> emailAttachments,
                          Map<String, String> emailRecipientNames,
                          byte[] consortiumPdfBytes) {
      if (!consortiumFeePeriod.isSendByEmail()) {
         return;
      }
      String consortiumName = consortiumFeePeriod.getConsortium().getName();
      LocalDate periodDate = consortiumFeePeriod.getPeriodDate();
      String monthNameSpanish = paymentService.getMonthName(periodDate);
      String year = String.valueOf(periodDate.getYear());
      
      String subject = String.format("Expensas del Consorcio %s - Periodo %s/%s",
                                    consortiumName, monthNameSpanish, year);
      
      for (Map.Entry<String, List<Pair<String, byte[]>>> entry : emailAttachments.entrySet()) {
         String email = entry.getKey();
         List<Pair<String, byte[]>> attachments = entry.getValue();
         String recipientName = emailRecipientNames.get(email);
         
         try {
            String emailText = consortiumFeePeriod.getNotes();
            
            // Enviar correo con adjuntos
            sendEmailWithAttachments(email, subject, emailText, attachments, consortiumPdfBytes);
            
            log.info("Email sent successfully to: {}", email);
         } catch (Exception e) {
            log.error("Error sending email to {}: {}", email, e.getMessage());
         }
      }
      consortiumFeePeriodService.updateConsortiumFeePeriodStatus(consortiumFeePeriod.getConsortiumFeePeriodId(), EConsortiumFeePeriodStatus.SENT);
   }
   
   private void sendEmailWithAttachments(
         String email, String subject, String text,
         List<Pair<String, byte[]>> departmentPdfs,
         byte[] consortiumPdf
   ) throws MessagingException, IOException {
      // Convertir la lista de destinatarios a un array
      String[] recipients = new String[] { email };
      
      // Preparar adjuntos
      Map<String, InputStream> attachments = new HashMap<>();
      
      // Adjuntar PDF general del consorcio
      attachments.put("expensas_consorcio.pdf", new ByteArrayInputStream(consortiumPdf));
      
      // Adjuntar PDFs de departamentos
      for (Pair<String, byte[]> departmentPdf : departmentPdfs) {
         attachments.put(departmentPdf.getLeft(), new ByteArrayInputStream(departmentPdf.getRight()));
      }
      
      // Enviar correo con adjuntos
      emailService.sendMessageWithAttachments(recipients, subject, text, attachments);
   }

   private String generateConsortiumPdfPath(ConsortiumFeePeriodEntity period) {
      LocalDate date = period.getPeriodDate();
      String month = paymentService.getMonthName(date);
      String year = String.valueOf(date.getYear());
      String dateForFileName = month + year;
      return String.format("consortium-fees/%d/%d/%d/expensas_%s_%s.pdf",
            period.getConsortium().getConsortiumId(),
            date.getYear(),
            date.getMonthValue(),
            period.getConsortium().getName(),
            dateForFileName
      );
   }
   
   private String generateDepartmentPdfPath(DepartmentFeeEntity departmentFee) {
      ConsortiumFeePeriodEntity period = departmentFee.getConsortiumFeePeriod();
      LocalDate date = period.getPeriodDate();
      String month = paymentService.getMonthName(date);
      String year = String.valueOf(date.getYear());
      String dateForFileName = month + year;
      return String.format("consortium-fees/%d/%d/%d/department/%d/detalle_expensas_%s_%s.pdf",
            period.getConsortium().getConsortiumId(),
            date.getYear(),
            date.getMonthValue(),
            departmentFee.getDepartment().getDepartmentId(),
            departmentFee.getDepartment().getCode(),
            dateForFileName
      );
   }

   private List<DepartmentFeeItemEntity> generateDepartmentFeeItemsFor(
         ConsortiumFeePeriodItemEntity periodItem,
         DepartmentEntity department,
         List<DepartmentEntity> activeDepartments,
         DepartmentFeeEntity departmentFee
   ) {
      List<DepartmentFeeItemEntity> departmentItems = new ArrayList<>();
      switch (periodItem.getConceptType()) {
         case ORDINARY, EXTRAORDINARY ->  {
            BigDecimal amount = calculateItemAmount(periodItem, department, activeDepartments);
            DepartmentFeeItemEntity departmentFeeItem = DepartmentFeeItemEntity
                  .builder()
                  .amount(amount)
                  .departmentFee(departmentFee)
                  .consortiumFeePeriodItem(periodItem)
                  .description(periodItem.getName())
                  .build();
            departmentItems.add(departmentFeeItem);
         }
         case AMENITY_USE -> {
            List<BookingEntity> departmentBookingsForPeriod = bookingService.getDepartmentBookingsForPeriod(department,
                  periodItem.getConsortiumFeePeriod().getPeriodDate());

            List<DepartmentFeeItemEntity> departmentFeeItems = departmentBookingsForPeriod.stream()
                  .map(bookingEntity -> {
                     String bookingDate = bookingEntity.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
                     String bookingShift = bookingEntity.getShift() == EShift.MORNING ? "MAÑANA" : "TARDE";
                     String description = String.format("Reserva de %s - %s %s", bookingEntity.getAmenity().getName(), bookingDate, bookingShift);
                     return DepartmentFeeItemEntity
                           .builder()
                           .amount(bookingEntity.getBookingCost())
                           .departmentFee(departmentFee)
                           .consortiumFeePeriodItem(periodItem)
                           .description(description)
                           .build();
                  }).toList();
            departmentItems.addAll(departmentFeeItems);
         }
         case ADJUSTMENT -> {
            List<AdjustmentEntity> departmentBookingsForPeriod = adjustmentService.getDepartmentAdjustmentsForPeriod(department,
                  periodItem.getConsortiumFeePeriod());
            List<DepartmentFeeItemEntity> departmentFeeItems = departmentBookingsForPeriod.stream()
                  .map(adjustment -> DepartmentFeeItemEntity
                        .builder()
                        .amount(adjustment.getOperationType() == EOperationType.CREDIT ? adjustment.getAmount().negate() : adjustment.getAmount())
                        .departmentFee(departmentFee)
                        .consortiumFeePeriodItem(periodItem)
                        .description(adjustment.getDescription())
                        .build()
                  ).toList();
            departmentItems.addAll(departmentFeeItems);
         }
      }
      return departmentItems;
   }

   private BigDecimal calculateItemAmount(ConsortiumFeePeriodItemEntity periodItem, DepartmentEntity department, List<DepartmentEntity> activeDepartments) {
      BigDecimal amount = periodItem.getAmount();
      int activeDepartmentsQuantity = activeDepartments.size();

      return switch (periodItem.getDistributionType()) {
         case EQUAL_SPLIT -> amount.divide(new BigDecimal(activeDepartmentsQuantity), 2, BigDecimal.ROUND_HALF_UP);
//         case AMENITY_USAGE -> bookingService.getDepartmentBookingCostForPeriod(department, periodItem.getConsortiumFeePeriod().getPeriodDate());
         default -> amount;
      };
   }

   @Override
   public void onWriteError(Exception exception, Chunk<? extends ConsortiumFeeWrapper> items) {
      List<Long> periodIds = items.getItems().stream()
                                  .map(wrapper -> wrapper.getConsortiumFeePeriod().getConsortiumFeePeriodId())
                                  .toList();
      if (!periodIds.isEmpty()) {
         consortiumFeePeriodService.updateConsortiumFeePeriodStatusByIds(periodIds, EConsortiumFeePeriodStatus.ERROR);
      }
   }
}
