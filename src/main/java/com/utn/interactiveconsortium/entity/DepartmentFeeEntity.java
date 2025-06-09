package com.utn.interactiveconsortium.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.utn.interactiveconsortium.enums.EPaymentStatus;

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
   @JoinColumn(name = "consortium_fee_period_id", nullable = false)
   private ConsortiumFeePeriodEntity consortiumFeePeriod;

   @ManyToOne
   @JoinColumn(name = "department_id", nullable = false)
   private DepartmentEntity department;

   private BigDecimal totalAmount;

   private BigDecimal dueAmount;

   private BigDecimal paidAmount;

   private LocalDate issueDate;

   private LocalDate dueDate;

   @Enumerated(EnumType.STRING)
   private EPaymentStatus paymentStatus;

   @OneToMany(mappedBy = "departmentFee", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<DepartmentFeeItemEntity> departmentFeeItems;

   @OneToMany
   private List<PaymentEntity> payments;

}
