package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.IssueReportDto;
import com.utn.interactiveconsortium.enums.EIssueReportStatus;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.exception.IssueReportStatusException;
import com.utn.interactiveconsortium.service.IssueReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/issueReport")
public class IssueReportController {

    private final IssueReportService issueReportService;

    private static final String ADMIN_AUTHORITY = "hasAnyAuthority('ROLE_ADMIN')";

    private static final String RESIDENT_AUTHORITY = "hasAnyAuthority('ROLE_RESIDENT')";

    @GetMapping("/consortium/{consortiumId}/admin")
    @PreAuthorize(ADMIN_AUTHORITY)
    public Page<IssueReportDto> getIssueReportAdmin(
            @PathVariable Long consortiumId,
            @RequestParam EIssueReportStatus status,
            Pageable pageable
    ) throws EntityNotFoundException {
        return issueReportService.getIssueReportAdmin(consortiumId, status, pageable);
    }

    @PostMapping
    @PreAuthorize(RESIDENT_AUTHORITY)
    public IssueReportDto createIssueReport(@RequestBody IssueReportDto issueReportDto) throws EntityNotFoundException {
        return issueReportService.createIssueReport(issueReportDto);
    }

    @DeleteMapping("/{issueReportId}")
    @PreAuthorize(RESIDENT_AUTHORITY)
    public void deleteIssueReport(@PathVariable Long issueReportId) throws EntityNotFoundException, IssueReportStatusException {
        issueReportService.deleteIssueReport(issueReportId);
    }

    @PatchMapping("/{issueReportId}/review")
    @PreAuthorize(ADMIN_AUTHORITY)
    public IssueReportDto setIssuerReportToStatusUnderReview(@PathVariable Long issueReportId) throws EntityNotFoundException {
        return issueReportService.setIssuerReportToStatusUnderReview(issueReportId);
    }

    @PutMapping("/resolve")
    @PreAuthorize(ADMIN_AUTHORITY)
    public IssueReportDto resolveIssueReport(@RequestBody IssueReportDto issueReportDto) throws EntityNotFoundException {
        return issueReportService.resolveIssueReport(issueReportDto);
    }

}
