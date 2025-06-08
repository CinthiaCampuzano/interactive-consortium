package com.utn.interactiveconsortium.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodItemEntity;
import com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsortiumFeePeriodDto {

   private Long consortiumFeePeriodId;

   private LocalDate periodDate;

   private LocalDate generationDate;

   private LocalDate dueDate;

   private EConsortiumFeePeriodStatus feePeriodStatus;

   private BigDecimal totalAmount;

   private String notes;

   private String pdfFilePath;

}
