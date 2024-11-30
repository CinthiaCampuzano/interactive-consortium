package com.utn.interactiveconsortium.entity;

import com.utn.interactiveconsortium.enums.EPaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "maintenance_fee_payment")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceFeePaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maintenanceFeePaymentId;

    @ManyToOne
    @JoinColumn(name = "maintenance_fee_id")
    private MaintenanceFeeEntity maintenanceFee;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @Enumerated(EnumType.STRING)
    private EPaymentStatus status;

    private LocalDateTime paymentDate;

    private BigDecimal amount;

}
