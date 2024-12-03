package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.config.MinioConfig;
import com.utn.interactiveconsortium.dto.MaintenanceFeePaymentDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.entity.MaintenanceFeePaymentEntity;
import com.utn.interactiveconsortium.enums.EPaymentStatus;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.MaintenanceFeePaymentMapper;
import com.utn.interactiveconsortium.repository.MaintenanceFeePaymentRepository;
import com.utn.interactiveconsortium.util.EmailService;
import com.utn.interactiveconsortium.util.MinioUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceFeePaymentService {

    private final MaintenanceFeePaymentRepository maintenanceFeePaymentRepository;

    private final MaintenanceFeePaymentMapper mapper;

    private final LoggedUserService loggedUserService;

    private final MinioConfig minioConfig;

    private final MinioUtils minioUtils;

    private final EmailService emailService;

    private static final String FILE_SEPARATOR = "/";

    private static final String MAINTENANCE_FEE_PAYMENT_SUBJECT = "Recibo de expensas del mes de %s - Consoricio %s";

    public Page<MaintenanceFeePaymentDto> getMaintenanceFeePayments(Long consortiumId, LocalDate period, Pageable page) {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        return mapper.toPage(maintenanceFeePaymentRepository.getMaintenanceFeePayments(consortiumId, period, associatedConsortiumIds, page));
    }

    @Transactional(rollbackOn = Exception.class)
    public MaintenanceFeePaymentDto updateMaintenanceFeePayment(
            MaintenanceFeePaymentDto maintenanceFeePaymentDto,
            MultipartFile file
    ) throws EntityNotFoundException, MessagingException, IOException {

        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(maintenanceFeePaymentDto.getMaintenanceFee().getConsortium().getConsortiumId())) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }

        MaintenanceFeePaymentEntity maintenanceFeePayment = maintenanceFeePaymentRepository.findById(maintenanceFeePaymentDto.getMaintenanceFeePaymentId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el pago de expensas"));

        maintenanceFeePayment.setStatus(maintenanceFeePaymentDto.getStatus());
        maintenanceFeePayment.setAmount(maintenanceFeePaymentDto.getAmount());
        maintenanceFeePayment.setPaymentDate(LocalDateTime.now());

        LocalDate period = maintenanceFeePayment.getMaintenanceFee().getPeriod();
        String fileName = generateFileName(maintenanceFeePayment.getMaintenanceFee().getConsortium(), maintenanceFeePayment.getDepartment(), period, file);
        maintenanceFeePayment.setFileName(fileName);

        MaintenanceFeePaymentEntity saved = maintenanceFeePaymentRepository.save(maintenanceFeePayment);

        String filePath = getFilePathFor(maintenanceFeePayment.getMaintenanceFee().getConsortium(), maintenanceFeePayment.getDepartment(), period, fileName);
        minioUtils.uploadFile(minioConfig.getBucketName(), file, filePath, file.getContentType());
        InputStream inputStream = minioUtils.getObject(minioConfig.getBucketName(), filePath);

        Set<String> mails = Set.of(maintenanceFeePayment.getDepartment().getResident().getMail(),
                maintenanceFeePayment.getDepartment().getPropietary().getMail());
        sendMaintenanceFeeMailWithAttachment(mails, maintenanceFeePayment.getMaintenanceFee().getConsortium().getName(), period, fileName, inputStream);

        return mapper.convertEntityToDto(saved);
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
        return consortium.getConsortiumId() +
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

    public void downloadMaintenanceFeePayment(Long maintenanceFeePaymentId, HttpServletResponse response) throws EntityNotFoundException, IOException {

        MaintenanceFeePaymentEntity maintenanceFeePayment = maintenanceFeePaymentRepository.findById(maintenanceFeePaymentId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el pago de expensas"));

        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(maintenanceFeePayment.getMaintenanceFee().getConsortium().getConsortiumId())) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }

        String fileNameWithPath = getFilePathFor(maintenanceFeePayment.getMaintenanceFee().getConsortium(), maintenanceFeePayment.getDepartment(), maintenanceFeePayment.getMaintenanceFee().getPeriod(), maintenanceFeePayment.getFileName());
        InputStream fileInputStream = minioUtils.getObject(minioConfig.getBucketName(), fileNameWithPath);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileNameWithPath.substring(fileNameWithPath.lastIndexOf("/") + 1));
        response.setContentType("application/force-download");
        response.setCharacterEncoding("UTF-8");
        IOUtils.copy(fileInputStream, response.getOutputStream());

    }

    public Page<MaintenanceFeePaymentDto> getMaintenanceFeePaymentsForPerson(Long consortiumId, LocalDate period, EPaymentStatus status, Pageable page) {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        Set<Long> associatedDepartmentsIds = new HashSet<>(loggedUserService.getAssociatedPropietaryDepartmentIds());
        associatedDepartmentsIds.addAll(loggedUserService.getAssociatedResidentDepartmentIds());
        return mapper.toPage(maintenanceFeePaymentRepository.getMaintenanceFeePaymentsForPerson(consortiumId, period, associatedConsortiumIds, associatedDepartmentsIds, page));
    }
}
