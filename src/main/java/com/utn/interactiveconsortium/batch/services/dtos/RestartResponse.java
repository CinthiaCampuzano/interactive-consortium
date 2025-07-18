package com.utn.interactiveconsortium.batch.services.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestartResponse implements Serializable {

   private LocalDateTime started;

   private Long jobId;

   private Long jobInstanceId;

   private String jobName;

   private String status;

   private Long restartId;

}
