package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.DepartmentDto;
import com.utn.interactiveconsortium.dto.PersonDto;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public Page<DepartmentDto> getDepartmentsByConsortium(@RequestParam Long consortiumId, Pageable page) {
        return departmentService.getDepartmentsByConsortium(consortiumId, page);
    }

    @GetMapping(value = "filterBy")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public Page<DepartmentDto> getDepartment(
            @RequestParam(required = false) Long idConsortium,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String ownerNameOrLastName,
            @RequestParam(required = false) String residentNameOrLastName,
            Pageable page) {
        return departmentService.getDepartment(idConsortium, code, ownerNameOrLastName, residentNameOrLastName, page);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public DepartmentDto createDepartment(@RequestBody DepartmentDto newDepartment) throws EntityAlreadyExistsException, EntityNotFoundException {
        return departmentService.createDepartment(newDepartment);

    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public void updateDepartment(@RequestBody @Validated DepartmentDto departmentToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        departmentService.updateDepartment(departmentToUpdate);
    }

    @DeleteMapping(value = "{idDepartment}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public void deleteDepartment(@PathVariable Long idDepartment) throws EntityNotFoundException {
        departmentService.deleteDepartment(idDepartment);

    }

}
