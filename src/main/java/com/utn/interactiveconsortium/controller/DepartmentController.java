package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.DepartmentDto;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public Page<DepartmentDto> getDepartments(Pageable page) {
        return departmentService.getDepartments(page);
    }

    @GetMapping(value = "filterBy")
    public Page<DepartmentDto> getDepartment(@RequestParam(required = false) String code, Pageable page){
        return departmentService.getDepartment(code, page);
    }

    @PostMapping
    public DepartmentDto createDepartment(@RequestBody DepartmentDto newDepartment) throws EntityAlreadyExistsException, EntityNotFoundException {
        return departmentService.createDepartment(newDepartment);

    }

    @PutMapping
    public void updateDepartment(@RequestBody DepartmentDto departmentToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        departmentService.updateDepartment(departmentToUpdate);
    }

    @DeleteMapping(value = "{idDepartment}")
    public void deleteDepartment(@PathVariable Long idDepartment) throws EntityNotFoundException {
        departmentService.deleteDepartment(idDepartment);

    }


}
