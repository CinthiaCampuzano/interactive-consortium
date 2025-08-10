package com.utn.interactiveconsortium.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AmenitiesBookingAvailableDto {

   private Long departmentId;

   private Long amenityId;

   private Long consortiumId;

   private Integer amenityMaxBooking;

   private Integer amenityBooking;

}
