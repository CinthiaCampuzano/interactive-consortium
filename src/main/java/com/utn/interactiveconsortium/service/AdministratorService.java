package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.AdministratorDto;
import com.utn.interactiveconsortium.entity.AdministratorEntity;
import com.utn.interactiveconsortium.entity.AppUser;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.AdministratorMapper;
import com.utn.interactiveconsortium.repository.AdministratorRepository;
import jakarta.transaction.Transactional;
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

    private final AppUserDetailsService appUserDetailsService;

    public Page<AdministratorDto> getAdministrators(Pageable page){
        return administratorMapper.toPage(administratorRepository.findAll(page));
    }

    public Page<AdministratorDto> getAdministrator( String name,String lastName, String mail, String dni, Pageable page){
        Page<AdministratorEntity> administratorEntityPage = administratorRepository.findAdministratorsByFilters(name, lastName, mail, dni, page);

        return administratorMapper.toPage(administratorEntityPage);
    }

    @Transactional(rollbackOn = Exception.class)
    public AdministratorDto createAdministrator(AdministratorDto newAdministrator) throws EntityAlreadyExistsException {
        if (administratorRepository.existsByDni(newAdministrator.getDni())) {
            throw new EntityAlreadyExistsException("Ya existe un administrador con ese dni");
        }
        if (administratorRepository.existsByMail(newAdministrator.getMail())) {
            throw new EntityAlreadyExistsException("Ya existe un administrador con ese mail");
        }

        AdministratorEntity newAdministratorEntity = administratorMapper.convertDtoToEntity(newAdministrator);
        administratorRepository.save(newAdministratorEntity);
        appUserDetailsService.register(newAdministratorEntity);

        return administratorMapper.convertEntityToDto(newAdministratorEntity);
    }

    @Transactional(rollbackOn = Exception.class)
    public void updateAdministrator(AdministratorDto administratorToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        boolean administratorExists = administratorRepository.existsById(administratorToUpdate.getAdministratorId());
        if (!administratorExists) {
            throw new EntityNotFoundException("No existe ese administrador");
        }

        AdministratorEntity administratorToUpdateEntity = administratorRepository.findById(administratorToUpdate.getAdministratorId()).get();
        AppUser appUser = appUserDetailsService.findByUsername(administratorToUpdateEntity.getMail());

        administratorToUpdateEntity.setName(administratorToUpdate.getName());
        administratorToUpdateEntity.setLastName(administratorToUpdate.getLastName());
        administratorToUpdateEntity.setMail(administratorToUpdate.getMail());
        administratorToUpdateEntity.setDni(administratorToUpdate.getDni());

        appUser.setUsername(administratorToUpdateEntity.getMail());

        try {
            administratorRepository.save(administratorToUpdateEntity);
            appUserDetailsService.updateAppUser(appUser);
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistsException("Ya existe este correo electronico o DNI para otro administrador");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteAdministrator(Long idAdministrator) throws EntityNotFoundException {
        AdministratorEntity administrator = administratorRepository.findById(idAdministrator)
                .orElseThrow(() -> new EntityNotFoundException("No existe ese administrador"));

        administratorRepository.deleteById(idAdministrator);
        appUserDetailsService.deleteByUsername(administrator.getMail());
    }

}
