package com.utn.interactiveconsortium.batch.services.dtos;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartRequest implements Serializable {

   private String jobName;

   private Map<String, String> params;

}
