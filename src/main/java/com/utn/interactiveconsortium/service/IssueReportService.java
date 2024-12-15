package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.IssueReportDto;
import com.utn.interactiveconsortium.entity.IssueReportEntity;
import com.utn.interactiveconsortium.enums.EIssueReportStatus;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.exception.IssueReportStatusException;
import com.utn.interactiveconsortium.mapper.IssueReportMapper;
import com.utn.interactiveconsortium.repository.IssueReportRepository;
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

    private final IssueReportMapper issueReportMapper;

    public Page<IssueReportDto> getIssueReport(Long consortiumId, EIssueReportStatus status, Pageable pageable) throws EntityNotFoundException {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(consortiumId)) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }
        return issueReportMapper.toPage(issueReportRepository.getIssueReport(consortiumId, status, pageable));
    }

    public IssueReportDto createIssueReport(IssueReportDto issueReportDto) throws EntityNotFoundException {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(issueReportDto.getConsortium().getConsortiumId())) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }
        IssueReportEntity issueReport = issueReportMapper.convertDtoToEntity(issueReportDto);
        issueReport.setStatus(EIssueReportStatus.PENDING);
        issueReport.setCreatedDate(LocalDateTime.now());

        return issueReportMapper.convertEntityToDto(issueReportRepository.save(issueReport));
    }

    public void deleteIssueReport(Long issueReportId) throws EntityNotFoundException, IssueReportStatusException {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(issueReportId)) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }

        IssueReportEntity issueReport = issueReportRepository.findById(issueReportId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el reclamo"));

        if (issueReport.getStatus() != EIssueReportStatus.PENDING) {
            throw new IssueReportStatusException("No se puede eliminar el reclamo");
        }
    }

    public IssueReportDto setIssuerReportToStatusUnderReview(Long issueReportId) throws EntityNotFoundException {
        List<Long> associatedConsortiumIds = loggedUserService.getAssociatedConsortiumIds();
        if (!associatedConsortiumIds.contains(issueReportId)) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }

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

        issueReport.setResponse(issueReport.getResponse());
        issueReport.setStatus(EIssueReportStatus.FINISHED);
        issueReport.setResponseDate(LocalDateTime.now());

        return issueReportMapper.convertEntityToDto(issueReportRepository.save(issueReport));
    }

}
