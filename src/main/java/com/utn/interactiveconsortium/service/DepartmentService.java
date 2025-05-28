package com.utn.interactiveconsortium.service;

import java.util.ArrayList;
import java.util.List;

import com.utn.interactiveconsortium.dto.DepartmentDto;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.DepartmentEntity;
import com.utn.interactiveconsortium.entity.PersonEntity;
import com.utn.interactiveconsortium.enums.EConsortiumType;
import com.utn.interactiveconsortium.exception.CustomIllegalArgumentException;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.DepartmentMapper;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;
import com.utn.interactiveconsortium.repository.DepartmentRepository;
import com.utn.interactiveconsortium.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ConsortiumRepository consortiumRepository;
    private final PersonRepository personRepository;
    private final DepartmentMapper departmentMapper;

    public Page<DepartmentDto> getDepartmentsByConsortium(Long consortiumId, Pageable pageable) {
        Page<DepartmentEntity> departmentEntities = departmentRepository.findByConsortium_ConsortiumId(consortiumId, pageable);
        return departmentMapper.toPage(departmentEntities);
    }


    public Page<DepartmentDto> getDepartment(Long idConsortium, String code, String ownerNameOrLastName, String residentNameOrLastName, Boolean active, Pageable page) {
        return departmentMapper.toPage(departmentRepository.findDepartmentByFilters(idConsortium, code, ownerNameOrLastName, residentNameOrLastName, active, page));
    }

    public DepartmentDto createDepartment(DepartmentDto newDepartment) throws EntityAlreadyExistsException, EntityNotFoundException {

        boolean departmentExists = departmentRepository.existsByCodeAndConsortiumId(newDepartment.getCode(),newDepartment.getConsortium().getConsortiumId());

        if (departmentExists) {
            throw new EntityAlreadyExistsException("Ya existe un departamento en ese piso con ese identificador");
        }

        DepartmentEntity newDepartmentEntity = departmentMapper.convertDtoToEntity(newDepartment);

        ConsortiumEntity consortium = consortiumRepository.findById(newDepartment.getConsortium().getConsortiumId())
                .orElseThrow(() -> new EntityNotFoundException("Consorcio no encontrado"));


        PersonEntity propietary = (newDepartment.getPropietary() != null && newDepartment.getPropietary().getPersonId() != null)
                ? personRepository.findById(newDepartment.getPropietary().getPersonId())
                .orElseThrow(() -> new EntityNotFoundException("Propietario no encontrado"))
                : null;


        PersonEntity resident = (newDepartment.getResident() != null && newDepartment.getResident().getPersonId() != null)
                ? personRepository.findById(newDepartment.getResident().getPersonId())
                .orElseThrow(() -> new EntityNotFoundException("Residente no encontrado"))
                : null;

        newDepartmentEntity.setConsortium(consortium);
        newDepartmentEntity.setPropietary(propietary);
        newDepartmentEntity.setResident(resident);


        departmentRepository.save(newDepartmentEntity);

        DepartmentDto newDepartmentDto = departmentMapper.convertEntityToDto(newDepartmentEntity);

        return newDepartmentDto;
    }

    public void massiveDepartmentCreation(ConsortiumEntity consortiumEntity) throws CustomIllegalArgumentException {
        if (consortiumEntity.getConsortiumType() != EConsortiumType.BUILDING) {
            throw new IllegalArgumentException("El consorcio debe ser de tipo edificio");
        }

        List<DepartmentEntity> departments = new ArrayList<>();

        for (int i = 1; i <= consortiumEntity.getFloors(); i++) {
            for (int j = 1; j <= consortiumEntity.getApartmentsPerFloor(); j++) {
                DepartmentEntity newDepartment = new DepartmentEntity();
                newDepartment.setCode(i + getLetterForDepartment(j));
                newDepartment.setConsortium(consortiumEntity);
                departments.add(newDepartment);
            }
        }

         departmentRepository.saveAll(departments);
    }

      public String getLetterForDepartment(int number) throws CustomIllegalArgumentException {
         if (number > 26) {
               throw new CustomIllegalArgumentException("limite de departamentos por piso alcanzado");
         }
         return String.valueOf((char) (number + 64));
      }

    public void updateDepartment(DepartmentDto departmentToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        boolean departmentExists = departmentRepository.existsById(departmentToUpdate.getDepartmentId());

        if (!departmentExists) {
            throw new EntityNotFoundException("No existe ese departamento");
        }

        DepartmentEntity departmentToUpdateEntity = departmentRepository.findById(departmentToUpdate.getDepartmentId()).get();

        boolean sameDepartment = departmentToUpdateEntity.getCode().equals(departmentToUpdate.getCode()) &&
                departmentToUpdateEntity.getConsortium().getConsortiumId().equals(departmentToUpdate.getConsortium().getConsortiumId());

        if (!sameDepartment) {
            boolean departmentExistsToUpdate = departmentRepository.existsByCodeAndConsortiumId(
                    departmentToUpdate.getCode(), departmentToUpdate.getConsortium().getConsortiumId());

            if (departmentExistsToUpdate) {
                throw new EntityAlreadyExistsException("Ya existe un departamento en ese piso con ese identificador");
            }
        }

        ConsortiumEntity consortium = consortiumRepository.findById(departmentToUpdate.getConsortium().getConsortiumId())
                .orElseThrow(() -> new EntityNotFoundException("Consorcio no encontrado"));

        PersonEntity propietary = (departmentToUpdate.getPropietary() != null && departmentToUpdate.getPropietary().getPersonId() != null)
                ? personRepository.findById(departmentToUpdate.getPropietary().getPersonId())
                .orElseThrow(() -> new EntityNotFoundException("Propietario no encontrado"))
                : null;

        PersonEntity resident = (departmentToUpdate.getResident() != null && departmentToUpdate.getResident().getPersonId() != null)
                ? personRepository.findById(departmentToUpdate.getResident().getPersonId())
                .orElseThrow(() -> new EntityNotFoundException("Residente no encontrado"))
                : null;

        departmentToUpdateEntity.setCode(departmentToUpdate.getCode());
        departmentToUpdateEntity.setPropietary(propietary);
        departmentToUpdateEntity.setResident(resident);

        departmentRepository.save(departmentToUpdateEntity);
    }

    public void deleteDepartment(Long idDepartment) throws EntityNotFoundException {
        boolean departmentExists = departmentRepository.existsById(idDepartment);

        if (!departmentExists) {
            throw new EntityNotFoundException("No existe ese departamento");
        }

        departmentRepository.deleteById(idDepartment);
    }

}
