package com.utn.interactiveconsortium.entity;

import java.math.BigDecimal;

import com.utn.interactiveconsortium.enums.EAdjustmentType;
import com.utn.interactiveconsortium.enums.EOperationType;

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
@Table(name = "adjustment")
public class AdjustmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "consortium_fee_period_id", nullable = false)
    private ConsortiumFeePeriodEntity consortiumFeePeriod;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity department;

    private String description;

    @Enumerated(EnumType.STRING)
    private EAdjustmentType adjustmentType;

    @Enumerated(EnumType.STRING)
    private EOperationType operationType;

    private BigDecimal amount;
}