package com.utn.interactiveconsortium.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.utn.interactiveconsortium.enums.EConsortiumFeePeriodStatus;

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
@Table(name = "consortium_fee_period")
public class ConsortiumFeePeriodEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long consortiumFeePeriodId;

   @ManyToOne
   @JoinColumn(name = "consortium_id", nullable = false)
   private ConsortiumEntity consortium;

   private LocalDate periodDate;

   private LocalDate generationDate;

   private LocalDate dueDate;

   @Enumerated(EnumType.STRING)
   private EConsortiumFeePeriodStatus  feePeriodStatus;

   private BigDecimal totalAmount;

   private String notes;

   private String pdfFilePath;

   @OneToMany(mappedBy = "consortiumFeePeriod", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<ConsortiumFeePeriodItemEntity> feePeriodItems;

}
