package com.utn.interactiveconsortium.entity;

import com.utn.interactiveconsortium.enums.EIssueReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "issue_report")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssueReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueReportId;

    private EIssueReportStatus status;

    private String subject;

    private String issue;

    private String response;

    private LocalDateTime createdDate;

    private LocalDateTime responseDate;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private PersonEntity person;

    @ManyToOne
    @JoinColumn(name = "consortium_id")
    private ConsortiumEntity consortium;

}
