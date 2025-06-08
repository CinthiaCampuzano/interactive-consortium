package com.utn.interactiveconsortium.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.utn.interactiveconsortium.enums.EPaymentMethod;

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
@Table(name = "payment")
public class PaymentEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long paymentId;

   @ManyToOne
   private  DepartmentFeeEntity departmentFee;

   private LocalDate paymentDate;

   private BigDecimal amount;

   private EPaymentMethod paymentMethod;

   //Numero de referencia de la transferencia
   private String referenceNumber;

   private String notes;
}
