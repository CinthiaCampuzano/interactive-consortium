package com.utn.interactiveconsortium.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class IssueReportCardsDto {

    private Long issueReportId;

    private Integer pending;

    private Integer underReview;

    private Integer resolved;

    private Integer total;

}
