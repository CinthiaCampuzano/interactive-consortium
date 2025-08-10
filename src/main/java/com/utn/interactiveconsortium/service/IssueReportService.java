package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.IssueReportCardsDto;
import com.utn.interactiveconsortium.dto.IssueReportDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.IssueReportEntity;
import com.utn.interactiveconsortium.enums.EIssueReportStatus;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.exception.IssueReportStatusException;
import com.utn.interactiveconsortium.mapper.IssueReportMapper;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;
import com.utn.interactiveconsortium.repository.IssueReportRepository;
import com.utn.interactiveconsortium.util.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueReportService {

    private final LoggedUserService loggedUserService;

    private final IssueReportRepository issueReportRepository;

    private final ConsortiumRepository consortiumRepository;

    private final IssueReportMapper issueReportMapper;

    private final EmailService emailService;

    private static final String ISSUER_CREATE_MESSAGE_TITLE_TEMPLATE = "Nuevo Reclamo Generado - Consorcio %s";

    private static final String ISSUER_CREATE_MESSAGE_BODY_TEMPLATE = "%s ha creado un nuevo reclamo en el consorcio %s";

    private static final String ISSUER_CLOSE_TITLE_TEMPLATE = "Reclamo Cerrado - Consorcio %s";

    private static final String ISSUER_CLOSE_MESSAGE_BODY_TEMPLATE = "El reclamo ha sido cerrado por el adminsitrador. Reclamo: %s";

    public Page<IssueReportDto> getIssueReportAdmin(Long consortiumId, EIssueReportStatus status, Pageable pageable) throws EntityNotFoundException {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(consortiumId)) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }
        return issueReportMapper.toPage(issueReportRepository.getIssueReport(consortiumId, status, pageable));
    }

    public Page<IssueReportDto> getIssueReportPerson(Long consortiumId, Pageable pageable) throws EntityNotFoundException {

        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();

        if (!associatedConsortiumIds.contains(consortiumId)) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }

        Long personId = loggedUserService.getLoggedPerson().getPersonId();

        return issueReportMapper.toPage(issueReportRepository.getIssueReportPerson(consortiumId, personId, pageable));

    }


    public IssueReportDto createIssueReport(IssueReportDto issueReportDto) throws EntityNotFoundException {
        if (issueReportDto.getIssue().isBlank() || issueReportDto.getSubject().isBlank()) {
            throw new EntityNotFoundException("El reclamo no puede estar vacio");
        }

        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(issueReportDto.getConsortium().getConsortiumId())) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }
        ConsortiumEntity consortium = consortiumRepository.findById(issueReportDto.getConsortium().getConsortiumId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el consorcio"));

        IssueReportEntity issueReport = issueReportMapper.convertDtoToEntity(issueReportDto);

        issueReport.setPerson(loggedUserService.getLoggedPerson());
        issueReport.setConsortium(consortium);
        issueReport.setStatus(EIssueReportStatus.PENDING);
        issueReport.setCreatedDate(LocalDateTime.now());

        IssueReportEntity savedIssueReport = issueReportRepository.save(issueReport);
        String consortiumName = savedIssueReport.getConsortium().getName();
        String personaName = savedIssueReport.getPerson().getName() + " " + savedIssueReport.getPerson().getLastName();
        String messageTitle = ISSUER_CREATE_MESSAGE_TITLE_TEMPLATE.formatted(consortiumName);
        String messageText = ISSUER_CREATE_MESSAGE_BODY_TEMPLATE.formatted(personaName, consortiumName);
        emailService.sendSimpleMessage(savedIssueReport.getConsortium().getAdministrator().getMail(), messageTitle, messageText);
        return issueReportMapper.convertEntityToDto(savedIssueReport);
    }

    public void deleteIssueReport(Long issueReportId) throws EntityNotFoundException, IssueReportStatusException {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();

        IssueReportEntity issueReport = issueReportRepository.findById(issueReportId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el reclamo"));

        if (!associatedConsortiumIds.contains(issueReport.getConsortium().getConsortiumId())) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }

        if (issueReport.getStatus() != EIssueReportStatus.PENDING) {
            throw new IssueReportStatusException("No se puede eliminar el reclamo");
        }

        issueReportRepository.delete(issueReport);
    }

    public IssueReportDto setIssuerReportToStatusUnderReview(Long issueReportId) throws EntityNotFoundException {
        IssueReportEntity issueReport = issueReportRepository.findById(issueReportId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el reclamo"));

        issueReport.setStatus(EIssueReportStatus.UNDER_REVIEW);
        issueReport.setResponseDate(LocalDateTime.now());
        return issueReportMapper.convertEntityToDto(issueReportRepository.save(issueReport));
    }

    public IssueReportDto resolveIssueReport(IssueReportDto issueReportDto) throws EntityNotFoundException {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(issueReportDto.getConsortium().getConsortiumId())) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }

        IssueReportEntity issueReport = issueReportRepository.findById(issueReportDto.getIssueReportId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el reclamo"));

        issueReport.setResponse(issueReportDto.getResponse());
        issueReport.setStatus(EIssueReportStatus.FINISHED);
        issueReport.setResponseDate(LocalDateTime.now());

        IssueReportEntity savedIssueReport = issueReportRepository.save(issueReport);
        String consortiumName = savedIssueReport.getConsortium().getName();
        String issuerReportTitle = savedIssueReport.getSubject();
        String messageTitle = ISSUER_CLOSE_TITLE_TEMPLATE.formatted(consortiumName);
        String messageText = ISSUER_CLOSE_MESSAGE_BODY_TEMPLATE.formatted(issuerReportTitle);
        emailService.sendSimpleMessage(savedIssueReport.getPerson().getMail(), messageTitle, messageText);
        return issueReportMapper.convertEntityToDto(savedIssueReport);
    }

    public IssueReportCardsDto getIssueReportCards(Long consortiumId) throws EntityNotFoundException {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(consortiumId)) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }

        List<IssueReportEntity> issueReports = issueReportRepository.findByConsortiumConsortiumId(consortiumId);
        Integer pending = 0;
        Integer underReview = 0;
        Integer resolved = 0;

        for (IssueReportEntity issueReport : issueReports) {
            switch (issueReport.getStatus()) {
                case PENDING:
                    pending++;
                    break;
                case UNDER_REVIEW:
                    underReview++;
                    break;
                case FINISHED:
                    resolved++;
                    break;
            }
        }

        return IssueReportCardsDto.builder()
                .pending(pending)
                .underReview(underReview)
                .resolved(resolved)
                .total(issueReports.size())
                .build();

    }

    public IssueReportCardsDto getIssueReportCardsPerson(Long consortiumId) throws EntityNotFoundException {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(consortiumId)) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }

        Long personId = loggedUserService.getLoggedPerson().getPersonId();

        List<IssueReportEntity> issueReports = issueReportRepository.findByConsortiumConsortiumIdAndPersonPersonId(consortiumId, personId);
        Integer pending = 0;
        Integer underReview = 0;
        Integer resolved = 0;

        for (IssueReportEntity issueReport : issueReports) {
            switch (issueReport.getStatus()) {
                case PENDING:
                    pending++;
                    break;
                case UNDER_REVIEW:
                    underReview++;
                    break;
                case FINISHED:
                    resolved++;
                    break;
            }
        }

        return IssueReportCardsDto.builder()
                .pending(pending)
                .underReview(underReview)
                .resolved(resolved)
                .total(issueReports.size())
                .build();

    }
}
