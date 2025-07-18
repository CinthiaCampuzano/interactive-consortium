package com.utn.interactiveconsortium.batch.services.dtos;

import org.springframework.batch.core.JobExecution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionsResponse {

   @JsonIgnoreProperties(ignoreUnknown = true, value = "stepExecutions")
   private JobExecution executions;

}
