package com.utn.interactiveconsortium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "maintenance_fee")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceFeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maintenanceFeeId;

    private LocalDate period;

    @ManyToOne()
    @JoinColumn(name = "consortium_id", nullable = false)
    private ConsortiumEntity consortium;

    private String fileName;

    private LocalDateTime uploadDate;

    @OneToMany(mappedBy = "maintenanceFee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaintenanceFeePaymentEntity> maintenanceFeePayments;

    private BigDecimal totalAmount;

}
