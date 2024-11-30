package com.utn.interactiveconsortium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "department")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long departmentId;

    private String code;

    @ManyToOne
    @JoinColumn(name = "consortium_id")
    private ConsortiumEntity consortium;

    @ManyToOne
    @JoinColumn(name = "propietary_id")
    private PersonEntity propietary;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private PersonEntity resident;

    @OneToMany(mappedBy = "department")
    private List<MaintenanceFeePaymentEntity> maintenanceFeePayments;

}
