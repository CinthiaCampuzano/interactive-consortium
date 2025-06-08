package com.utn.interactiveconsortium.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.utn.interactiveconsortium.enums.EConsortiumFeeConceptType;

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
@Table(name = "department_fee_item")
public class DepartmentFeeItemEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long departmentFeeItemId;

   @ManyToOne
   private DepartmentFeeEntity departmentFee;

   @ManyToOne
   private ConsortiumFeePeriodItemEntity consortiumFeePeriodItem;

   private BigDecimal proportional;

   private BigDecimal amount;

}
