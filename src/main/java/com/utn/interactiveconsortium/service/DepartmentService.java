package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.DepartmentDto;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.DepartmentMapper;
import com.utn.interactiveconsortium.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public Page<DepartmentDto> getDepartments(Pageable page){
        return departmentMapper.toPage(departmentRepository.findAll(page));
    }

    public Page<DepartmentDto> getDepartment(String code, Pageable page){
        Page<DepartmentEntity> departmentEntityPage = departmentRepository.findAdministratorsByFilters(code, page);

        return departmentMapper.toPage(departmentEntityPage);
    }
    public DepartmentDto createDepartment(DepartmentDto newDepartment) throws EntityAlreadyExistsException{

        boolean departmentExists = departmentRepository.existsByCode(newDepartment.getCode());

        if (departmentExists) {
            throw new EntityAlreadyExistsException("Ya existe un departamento en ese piso con ese identificador");
        }

        DepartmentEntity newDepartmentEntity = departmentMapper.convertDtoToEntity(newDepartment);

        departmentRepository.save(newDepartmentEntity);

        DepartmentDto newDepartmentDto = departmentMapper.convertEntityToDto(newDepartmentEntity);

        return newDepartmentDto;
    }

    public void updateDepartment(DepartmentDto departmentToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        boolean departmentExists = departmentRepository.existsById(departmentToUpdate.getDepartmentId());

        if (!departmentExists) {
            throw new EntityNotFoundException("No existe ese departamento");
        }

        DepartmentEntity departmentToUpdateEntity = departmentRepository.findById(departmentToUpdate.getDepartmentId()).get();

        departmentToUpdateEntity.setCode(departmentToUpdate.getCode());

        try {
            departmentRepository.save(departmentToUpdateEntity);
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistsException("Ya existe un departamento en ese piso con ese identificador");
        }

    }

    public void deleteDepartment(Long idDepartment) throws EntityNotFoundException {
        boolean departmentExists = departmentRepository.existsById(idDepartment);

        if (!departmentExists) {
            throw new EntityNotFoundException("No existe ese departamento");
        }

        departmentRepository.deleteById(idDepartment);
    }

}
