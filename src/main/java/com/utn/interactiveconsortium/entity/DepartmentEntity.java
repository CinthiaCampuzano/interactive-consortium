package com.utn.interactiveconsortium.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consortium_id")
    private ConsortiumEntity consortium;
}
