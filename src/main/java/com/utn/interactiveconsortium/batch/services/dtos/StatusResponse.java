package com.utn.interactiveconsortium.batch.services.dtos;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusResponse implements Serializable {

   public static final String COMPLETED_STATUS = "COMPLETED";

   public static final String FAILED_STATUS = "FAILED";

   public static final String ABANDONED_STATUS = "ABANDONED";

   public static final String STOPPED_STATUS = "STOPPED";

   public static final String NOT_FOUND_STATUS = "NOT_FOUND";

   private String status;

}
