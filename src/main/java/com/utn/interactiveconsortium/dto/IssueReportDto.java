package com.utn.interactiveconsortium.dto;

import com.utn.interactiveconsortium.enums.EIssueReportStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class IssueReportDto implements Serializable {

    private Long issueReportId;

    private EIssueReportStatus status;

    private String subject;

    private String issue;

    private String response;

    private LocalDateTime createdDate;

    private LocalDateTime responseDate;

    private PersonDto person;

    private ConsortiumDto consortium;
}
