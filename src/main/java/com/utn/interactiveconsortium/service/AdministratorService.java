package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.AdministratorDto;
import com.utn.interactiveconsortium.entity.AdministratorEntity;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.AdministratorMapper;
import com.utn.interactiveconsortium.repository.AdministratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor

public class AdministratorService {

    private final AdministratorRepository administratorRepository;
    private final AdministratorMapper administratorMapper;

    public Page<AdministratorDto> getAdministrators(Pageable page){
        return administratorMapper.toPage(administratorRepository.findAll(page));
    }

    public Page<AdministratorDto> getAdministrator( String name,String lastName, String mail, String dni, Pageable page){
        Page<AdministratorEntity> administratorEntityPage = administratorRepository.findAdministratorsByFilters(name, lastName, mail, dni, page);

        return administratorMapper.toPage(administratorEntityPage);

    }

    public AdministratorDto createAdministrator(AdministratorDto newAdministrator) throws EntityAlreadyExistsException {

        if(administratorRepository.existsByDni(newAdministrator.getDni())){
            throw new EntityAlreadyExistsException("Ya existe un administrador con ese dni");
        }

        if(administratorRepository.existsByMail(newAdministrator.getMail())){
            throw new EntityAlreadyExistsException("Ya existe un administrador con ese mail");
        }

        AdministratorEntity newAdministratorEntity = administratorMapper.convertDtoToEntity(newAdministrator);

        administratorRepository.save(newAdministratorEntity);

        AdministratorDto newAdministratorDto = administratorMapper.convertEntityToDto(newAdministratorEntity);

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
