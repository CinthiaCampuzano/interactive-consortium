package com.utn.interactiveconsortium.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "consortium")
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsortiumEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long consortiumId;

    private String name;

    private String address;

    private String city;

    private String province;

}
