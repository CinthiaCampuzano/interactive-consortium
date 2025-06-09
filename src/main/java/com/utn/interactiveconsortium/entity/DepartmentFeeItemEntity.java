package com.utn.interactiveconsortium.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
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
@Table(name = "department_fee_item")
public class DepartmentFeeItemEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long departmentFeeItemId;

   @ManyToOne
   @JoinColumn(name = "department_fee_id", nullable = false)
   private DepartmentFeeEntity departmentFee;

   @ManyToOne
   @JoinColumn(name = "consortium_fee_period_item_id", nullable = false)
   private ConsortiumFeePeriodItemEntity consortiumFeePeriodItem;

   private BigDecimal proportional;

   private BigDecimal amount;

}
