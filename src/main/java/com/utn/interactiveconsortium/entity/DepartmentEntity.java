package com.utn.interactiveconsortium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

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

    //TODO legacy class
//    @OneToMany(mappedBy = "department")
//    private List<MaintenanceFeePaymentEntity> maintenanceFeePayments;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepartmentFeeEntity> departmentFees;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingEntity> bookings;

    private Boolean active;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DepartmentEntity that = (DepartmentEntity) o;
        return Objects.equals(departmentId, that.departmentId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(departmentId);
        result = 31 * result + Objects.hashCode(code);
        result = 31 * result + Objects.hashCode(consortium);
        result = 31 * result + Objects.hashCode(propietary);
        result = 31 * result + Objects.hashCode(resident);
        result = 31 * result + Objects.hashCode(departmentFees);
        result = 31 * result + Objects.hashCode(bookings);
        result = 31 * result + Objects.hashCode(active);
        return result;
    }
}
