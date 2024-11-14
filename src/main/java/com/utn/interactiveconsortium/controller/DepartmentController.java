package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.DepartmentDto;
import com.utn.interactiveconsortium.dto.PersonDto;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

//    @GetMapping
//    public Page<DepartmentDto> getDepartments(Pageable page) {
//        return departmentService.getDepartments(page);
//    }

//    El administrador puede obtener todos los departamentos de un consorcio
    @GetMapping
    public Page<DepartmentDto> getDepartmentsByConsortium(@RequestParam Long consortiumId, Pageable page) {
        return departmentService.getDepartmentsByConsortium(consortiumId, page);
    }

//    El administrador puede obtener todos los departamentos de un consorcio por filtro
    @GetMapping(value = "filterBy")
    public Page<DepartmentDto> getDepartment(
            @RequestParam(required = false) Long idConsortium,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String ownerNameOrLastName,
            @RequestParam(required = false) String residentNameOrLastName,
            Pageable page) {
        return departmentService.getDepartment(idConsortium, code, ownerNameOrLastName, residentNameOrLastName, page);
    }

//    El administrador puede crear un departamento
    @PostMapping
    public DepartmentDto createDepartment(@RequestBody DepartmentDto newDepartment) throws EntityAlreadyExistsException, EntityNotFoundException {
        return departmentService.createDepartment(newDepartment);

    }

//    El administrador puede actualizar un departamento
    @PutMapping
    public void updateDepartment(@RequestBody DepartmentDto departmentToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        departmentService.updateDepartment(departmentToUpdate);
    }


//    El administrador puede eliminar un departamento
    @DeleteMapping(value = "{idDepartment}")
    public void deleteDepartment(@PathVariable Long idDepartment) throws EntityNotFoundException {
        departmentService.deleteDepartment(idDepartment);

    }


}
