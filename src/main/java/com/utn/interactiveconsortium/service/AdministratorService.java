package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.AdministratorDto;
import com.utn.interactiveconsortium.entity.AdministratorEntity;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.repository.AdministratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministratorService {

    private final AdministratorRepository administratorRepository;

    public List<AdministratorDto> getAdministrators(){
        List<AdministratorEntity> administratorEntityList = administratorRepository.findAll();

        List<AdministratorDto> administratorDtoList = administratorEntityList.stream().
                map(administrator -> AdministratorDto.builder()
                        .administratorId(administrator.getAdministratorId())
                        .name(administrator.getName())
                        .lastName(administrator.getLastName())
                        .mail(administrator.getMail())
                        .dni(administrator.getDni())
                        .build()
                ).toList();
        return administratorDtoList;
    }

    public List<AdministratorDto> getAdministrator(String nameAdministrator){
        List<AdministratorEntity> administratorEntityList = administratorRepository.findAByName(nameAdministrator);

        List<AdministratorDto> administratorDtoList = administratorEntityList.stream().
                map(administrator -> AdministratorDto.builder()
                        .administratorId(administrator.getAdministratorId())
                        .name(administrator.getName())
                        .lastName(administrator.getLastName())
                        .mail(administrator.getMail())
                        .dni(administrator.getDni())
                        .build()
                ).toList();
        return administratorDtoList;

    }

    public AdministratorDto createAdministrator(AdministratorDto newAdministrator) throws EntityAlreadyExistsException {

        if(administratorRepository.existsByDni(newAdministrator.getDni())){
            throw new EntityAlreadyExistsException("Ya existe un administrador con ese dni");
        }

        if(administratorRepository.existsByMail(newAdministrator.getMail())){
            throw new EntityAlreadyExistsException("Ya existe un administrador con ese mail");
        }

        AdministratorEntity newAdministratorEntity = AdministratorEntity.builder()
                .name(newAdministrator.getName())
                .lastName(newAdministrator.getLastName())
                .mail(newAdministrator.getMail())
                .dni(newAdministrator.getDni())
                .build();

        administratorRepository.save(newAdministratorEntity);

        AdministratorDto newAdministratorDto = AdministratorDto.builder()
                .administratorId(newAdministratorEntity.getAdministratorId())
                .name(newAdministratorEntity.getName())
                .lastName(newAdministratorEntity.getLastName())
                .mail(newAdministratorEntity.getMail())
                .dni(newAdministratorEntity.getDni())
                .build();

        return newAdministratorDto;
    }

    public void updateAdministrator(AdministratorDto administratorToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        boolean administratorExists = administratorRepository.existsById(administratorToUpdate.getAdministratorId());

        if (!administratorExists) {
            throw new EntityNotFoundException("No existe ese administrador");
        }

        AdministratorEntity administratorToUpdateEntity = administratorRepository.findById(administratorToUpdate.getAdministratorId()).get();

        administratorToUpdateEntity.setName(administratorToUpdate.getName());
        administratorToUpdateEntity.setLastName(administratorToUpdate.getLastName());
        administratorToUpdateEntity.setMail(administratorToUpdate.getMail());

        try {
            administratorRepository.save(administratorToUpdateEntity);
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistsException("Ya existe este correo electronico para otro administrador");
        }
    }

       public void deleteAdministrator(Long idAdministrator) throws EntityNotFoundException{
            boolean administratorExists = administratorRepository.existsById(idAdministrator);

            if(!administratorExists) {
                throw new EntityNotFoundException("No se encontro el administrador");
            }
            administratorRepository.deleteById(idAdministrator);
        }
}
