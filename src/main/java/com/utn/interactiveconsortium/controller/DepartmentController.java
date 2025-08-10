package com.utn.interactiveconsortium.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utn.interactiveconsortium.dto.DepartmentDto;
import com.utn.interactiveconsortium.exception.CustomGenericException;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.DepartmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_RESIDENT')")
    public Page<DepartmentDto> getDepartmentsByConsortium(@RequestParam Long consortiumId, Pageable page) {
        return departmentService.getDepartmentsByConsortium(consortiumId, page);
    }

    @GetMapping("/consortium/{consortiumId}/list")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public List<DepartmentDto> getDepartmentsListByConsortiumId(@PathVariable Long consortiumId) {
        return departmentService.getDepartmentsListByConsortiumId(consortiumId);
    }


    @GetMapping(value = "filterBy")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public Page<DepartmentDto> getDepartment(
            @RequestParam(required = false) Long idConsortium,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String ownerNameOrLastName,
            @RequestParam(required = false) String residentNameOrLastName,
            @RequestParam(required = false) Boolean active,
            Pageable page) {
        return departmentService.getDepartment(idConsortium, code, ownerNameOrLastName, residentNameOrLastName, active, page);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public DepartmentDto createDepartment(@RequestBody DepartmentDto newDepartment) throws EntityAlreadyExistsException, EntityNotFoundException {
        return departmentService.createDepartment(newDepartment);

    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public void updateDepartment(@RequestBody @Validated DepartmentDto departmentToUpdate)
          throws EntityNotFoundException, EntityAlreadyExistsException, CustomGenericException {
        departmentService.updateDepartment(departmentToUpdate);
    }

    @DeleteMapping(value = "{idDepartment}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public void deleteDepartment(@PathVariable Long idDepartment) throws EntityNotFoundException {
        departmentService.deleteDepartment(idDepartment);

    }

}
