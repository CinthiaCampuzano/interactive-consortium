package com.utn.interactiveconsortium.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DepartmentDto {
    private Long departmentId;

    @NotBlank
    private String code;

    private ConsortiumDto consortium;

    private PersonDto propietary;

    private PersonDto resident;

}
