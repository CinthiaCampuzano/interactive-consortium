package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.config.MinioConfig;
import com.utn.interactiveconsortium.dto.MaintenanceFeeDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.MaintenanceFeeEntity;
import com.utn.interactiveconsortium.entity.MaintenanceFeePaymentEntity;
import com.utn.interactiveconsortium.enums.EPaymentStatus;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.exception.InvalidMaintenanceFeePeriodException;
import com.utn.interactiveconsortium.mapper.MaintenanceFeeMapper;
import com.utn.interactiveconsortium.repository.MaintenanceFeePaymentRepository;
import com.utn.interactiveconsortium.repository.MaintenanceFeeRepository;
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
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceFeeService {

    private final MaintenanceFeeRepository maintenanceFeeRepository;

    private final MaintenanceFeePaymentRepository maintenanceFeePaymentRepository;

    private final ConsortiumService consortiumService;

    private final MaintenanceFeeMapper maintenanceFeeMapper;

    private final MinioConfig minioConfig;

    private final MinioUtils minioUtils;

    private final EmailService emailService;

    private static final String MAINTENANCE_FEE_SUBJECT = "Expensas del mes de %s - Consoricio %s";

    private static final String FILE_SEPARATOR = "/";

    @Transactional(rollbackOn = Exception.class)
    public MaintenanceFeeDto create(Long consortiumId, MultipartFile file) throws EntityNotFoundException, EntityAlreadyExistsException, MessagingException, IOException {
        LocalDate period = LocalDate.now().withDayOfMonth(1).minusMonths(1);
        ConsortiumEntity consortium = consortiumService.findConsortiumById(consortiumId);
        Optional<MaintenanceFeeEntity> maintenanceFeeEntity = maintenanceFeeRepository.findByConsortiumAndPeriod(consortium, period);
        if (maintenanceFeeEntity.isPresent()) {
            throw new EntityAlreadyExistsException("Maintenance fee already exists");
        }

        String fileName = generateFileName(consortium, period, file);
        String filePath = getFilePathFor(consortium, period, fileName);
        minioUtils.uploadFile(minioConfig.getBucketName(), file, filePath, file.getContentType());

        MaintenanceFeeEntity newMaintenanceFeeEntity = MaintenanceFeeEntity.builder()
                .period(period)
                .consortium(consortium)
                .fileName(fileName)
                .uploadDate(LocalDateTime.now())
                .build();
        maintenanceFeeRepository.save(newMaintenanceFeeEntity);

        List<MaintenanceFeePaymentEntity> maintenanceFeePayments = new ArrayList<>();
        consortium.getDepartments().forEach(department -> {
            maintenanceFeePayments.add(MaintenanceFeePaymentEntity.builder()
                    .department(department)
                    .status(EPaymentStatus.PENDING)
                    .maintenanceFee(newMaintenanceFeeEntity)
                    .paymentDate(null)
                    .amount(null)
                    .build());
        });
        maintenanceFeePaymentRepository.saveAll(maintenanceFeePayments);

        InputStream inputStream = minioUtils.getObject(minioConfig.getBucketName(), filePath);
        Set<String> mails = new HashSet<>();
        consortium.getDepartments().forEach(department -> {
            mails.add(department.getPropietary().getMail());
            mails.add(department.getResident().getMail());
        });

        sendMaintenanceFeeMailWithAttachment(mails, newMaintenanceFeeEntity.getConsortium().getName(), newMaintenanceFeeEntity.getPeriod(), fileName, inputStream);

        return maintenanceFeeMapper.convertEntityToDto(newMaintenanceFeeEntity);
    }

    private String generateFileName(ConsortiumEntity consortium, LocalDate period, MultipartFile file) {
        return "Expensas" +
                consortium.getName().replace(' ', '_') +
                "_" +
                period.getYear() +
                "_" +
                period.getMonthValue() +
                getFileExtension(file);
    }

    private String getFilePathFor(ConsortiumEntity consortium, LocalDate period, String fileName) {
        return consortium.getConsortiumId() +
                FILE_SEPARATOR +
                period.getYear() +
                FILE_SEPARATOR +
                period.getMonthValue() +
                FILE_SEPARATOR +
                fileName;
    }

    private String getFileExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName != null && originalFileName.contains(".")) {
            return originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return "";
    }

    public Page<MaintenanceFeeDto> getMaintenanceFees(Long idConsortium, Pageable page) {
        return maintenanceFeeMapper.toPage(maintenanceFeeRepository.findByConsortiumConsortiumIdOrderByPeriodDesc(idConsortium, page));
    }

    public void deleteByMaintenanceFeeId(Long maintenanceFeeId) throws EntityNotFoundException, InvalidMaintenanceFeePeriodException {
        MaintenanceFeeEntity maintenanceFee = maintenanceFeeRepository.findById(maintenanceFeeId)
                .orElseThrow(() -> new EntityNotFoundException("Maintenance fee not found"));
        if (LocalDate.now().withDayOfMonth(1).minusMonths(1).isEqual(maintenanceFee.getPeriod())) {
            throw new InvalidMaintenanceFeePeriodException("Maintenance fee period is not valid");
        }
        maintenanceFeeRepository.delete(maintenanceFee);
    }

    public void downloadMaintenanceFee(Long maintenanceFeeId, HttpServletResponse response) throws EntityNotFoundException, MessagingException, IOException {
            MaintenanceFeeEntity maintenanceFee = maintenanceFeeRepository.findById(maintenanceFeeId)
                    .orElseThrow(() -> new EntityNotFoundException("Maintenance fee not found"));
            String fileNameWithPath = getFilePathFor(maintenanceFee.getConsortium(), maintenanceFee.getPeriod(), maintenanceFee.getFileName());
            InputStream fileInputStream = minioUtils.getObject(minioConfig.getBucketName(), fileNameWithPath);
            response.setHeader("Content-Disposition", "attachment;filename=" + fileNameWithPath.substring(fileNameWithPath.lastIndexOf("/") + 1));
            response.setContentType("application/force-download");
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(fileInputStream, response.getOutputStream());

    }


    public void sendMaintenanceFeeMail(List<String> mails, String consortiumName, LocalDate period) {
        String subject = String.format(MAINTENANCE_FEE_SUBJECT, getMonthName(period) + " " + period.getYear(), consortiumName);
        for (String mail : mails) {
            emailService.sendSimpleMessage(mail, subject, "esto es un texto de prueba");
        }
    }

    public void sendMaintenanceFeeMailWithAttachment(Set<String> mails, String consortiumName, LocalDate period, String fileName, InputStream file) throws MessagingException, IOException {
        String subject = String.format(MAINTENANCE_FEE_SUBJECT, getMonthName(period) + " " + period.getYear(), consortiumName);
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
