package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.PersonDto;
import com.utn.interactiveconsortium.entity.AppUser;
import com.utn.interactiveconsortium.entity.PersonEntity;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.PersonMapper;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;
import com.utn.interactiveconsortium.repository.PersonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    private final ConsortiumRepository consortiumRepository;

    private final PersonMapper personMapper;

    private final AppUserDetailsService appUserDetailsService;

    public Page<PersonDto> getPersons(Pageable page) {
        return personMapper.toPage(personRepository.findAll(page));
    }

    public Page<PersonDto> getPerson(String name, String lastName, String mail, String dni, Pageable page) {
        return personMapper.toPage(personRepository.findPersonByFilters(name, lastName, mail, dni, page));
    }

    public PersonDto getPersonByDni(String dni) throws EntityNotFoundException {
        PersonEntity personEntity = personRepository.findByDni(dni)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro la persona"));

        return personMapper.convertEntityToDto(personEntity);
    }

    public List<PersonDto> getOwnersByConsortium(Long consortiumId) throws EntityNotFoundException {
        consortiumRepository.findById(consortiumId)
                            .orElseThrow(() -> new EntityNotFoundException("No se encontro el consorcio"));

        List<PersonEntity> owners = personRepository.findOwnersByConsortiumId(consortiumId);
        return owners.stream()
                .map(personMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public List<PersonDto> getResidentsByConsortium(Long consortiumId) throws EntityNotFoundException {
        consortiumRepository.findById(consortiumId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el consorcio"));

        List<PersonEntity> residents = personRepository.findResidentsByConsortiumId(consortiumId);
        return residents.stream()
                .map(personMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackOn = Exception.class)
    public PersonDto createPerson(PersonDto newPerson) throws EntityAlreadyExistsException {

        if (personRepository.existsByDni(newPerson.getDni())) {
            throw new EntityAlreadyExistsException("Ya existe una persona con ese dni");
        }

        if (personRepository.existsByMail(newPerson.getMail())){
            throw new EntityAlreadyExistsException("Ya existe una persona con ese mail");
        }

        PersonEntity newPersonEntity = personMapper.convertDtoToEntity(newPerson);

        personRepository.save(newPersonEntity);
        appUserDetailsService.register(newPersonEntity);

        PersonDto newPersonDto = personMapper.convertEntityToDto(newPersonEntity);

        return newPersonDto;
    }

    @Transactional(rollbackOn = Exception.class)
    public void updatePerson(PersonDto personToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        PersonEntity personToUpdateEntity = personRepository.findById(personToUpdate.getPersonId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el persona"));

        AppUser appUser = appUserDetailsService.findByUsername(personToUpdate.getMail());

        personToUpdateEntity.setName(personToUpdate.getName());
        personToUpdateEntity.setLastName(personToUpdate.getLastName());
        personToUpdateEntity.setMail(personToUpdate.getMail());
        personToUpdateEntity.setDni(personToUpdate.getDni());
        personToUpdateEntity.setPhoneNumber(personToUpdate.getPhoneNumber());

        appUser.setUsername(personToUpdate.getMail());

        try {
            personRepository.save(personToUpdateEntity);
            appUserDetailsService.updateAppUser(appUser);
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistsException("Ya existe este correo electronico o Dni para otra persona");
        }
    }

    public void deletePerson(Long idPerson) throws EntityNotFoundException {
        boolean personExists = personRepository.existsById(idPerson);

        if (!personExists) {
            throw new EntityNotFoundException("No existe esa persona");
        }

        personRepository.deleteById(idPerson);
    }

}