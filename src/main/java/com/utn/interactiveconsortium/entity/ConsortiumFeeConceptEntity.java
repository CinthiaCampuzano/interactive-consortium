package com.utn.interactiveconsortium.entity;

import java.math.BigDecimal;

import com.utn.interactiveconsortium.enums.EConsortiumFeeConceptType;
import com.utn.interactiveconsortium.enums.EConsortiumFeeDistributionType;
import com.utn.interactiveconsortium.enums.EConsortiumFeeType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
@Table(name = "consortium_fee_concept")
public class ConsortiumFeeConceptEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long consortiumFeeConceptId;

   @ManyToOne
   @JoinColumn(name = "consortium_id")
   private ConsortiumEntity consortium;

   private String name;

   private String description;

   private BigDecimal defaultAmount;

   @Enumerated(EnumType.STRING)
   private EConsortiumFeeConceptType conceptType;

   @Builder.Default
   @Enumerated(EnumType.STRING)
   private EConsortiumFeeType feeType = EConsortiumFeeType.COST;

   @Builder.Default
   @Enumerated(EnumType.STRING)
   private EConsortiumFeeDistributionType distributionType = EConsortiumFeeDistributionType.EQUAL_SPLIT;

   private boolean active;

}
