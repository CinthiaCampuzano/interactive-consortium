package com.utn.interactiveconsortium.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DepartmentDto {
    private Long departmentId;

    private String code;

    private ConsortiumDto consortium;

    private PersonDto propietary;

    private PersonDto resident;

}
