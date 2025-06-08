package com.utn.interactiveconsortium.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "department_fee")
public class DepartmentFeeEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long departmentFeeId;

   @ManyToOne
   private ConsortiumFeePeriodEntity consortiumFeePeriod;

   @ManyToOne
   private DepartmentEntity department;

   private BigDecimal totalAmount;

   private BigDecimal dueAmount;

   private BigDecimal paidAmount;

   private LocalDate issueDate;

   private LocalDate dueDate;

   @OneToMany
   private List<DepartmentFeeItemEntity> departmentFeeItems;

   @OneToMany
   private List<PaymentEntity> payments;

}
