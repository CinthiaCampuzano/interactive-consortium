package com.utn.interactiveconsortium.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.utn.interactiveconsortium.enums.EConsortiumFeeConceptType;
import com.utn.interactiveconsortium.enums.EConsortiumFeeDistributionType;
import com.utn.interactiveconsortium.enums.EConsortiumFeeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "consortium_fee_period_item")
public class ConsortiumFeePeriodItemEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long consortiumFeePeriodItemId;

   @ManyToOne
   @JoinColumn(name = "consortium_fee_period_id")
   private ConsortiumFeePeriodEntity consortiumFeePeriod;

   private String name;

   private String description;

   private BigDecimal amount;

   @Enumerated(EnumType.STRING)
   private EConsortiumFeeConceptType conceptType;

   @Builder.Default
   @Enumerated(EnumType.STRING)
   private EConsortiumFeeType feeType = EConsortiumFeeType.COST;

   @Builder.Default
   @Enumerated(EnumType.STRING)
   private EConsortiumFeeDistributionType distributionType = EConsortiumFeeDistributionType.EQUAL_SPLIT;

}
