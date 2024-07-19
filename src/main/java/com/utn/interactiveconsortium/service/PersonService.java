package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.PersonDto;
import com.utn.interactiveconsortium.entity.PersonEntity;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.PersonMapper;
import com.utn.interactiveconsortium.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public Page<PersonDto> getPersons(Pageable page) {
        return personMapper.toPage(personRepository.findAll(page));
    }

    public Page<PersonDto> getPerson(String name, String lastName, String mail, String dni, Pageable page) {
        return personMapper.toPage(personRepository.findPersonByFilters(name, lastName, mail, dni, page));
    }

    public PersonDto createPerson(PersonDto newPerson) throws EntityAlreadyExistsException {

        if (personRepository.existsByDni(newPerson.getDni())) {
            throw new EntityAlreadyExistsException("Ya existe una persona con ese dni");
        }

        if (personRepository.existsByMail(newPerson.getMail())){
            throw new EntityAlreadyExistsException("Ya existe una persona con ese mail");
        }

        PersonEntity newPersonEntity = personMapper.convertDtoToEntity(newPerson);

        personRepository.save(newPersonEntity);

        PersonDto newPersonDto = personMapper.convertEntityToDto(newPersonEntity);

        return newPersonDto;
    }

    public void updatePerson(PersonDto personToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        boolean personExists = personRepository.existsById(personToUpdate.getPersonId());

        if (!personExists) {
            throw new EntityNotFoundException("No existe esa persona");
        }

        PersonEntity personToUpdateEntity = personRepository.findById(personToUpdate.getPersonId()).get();

        personToUpdateEntity.setName(personToUpdate.getName());
        personToUpdateEntity.setLastName(personToUpdate.getLastName());
        personToUpdateEntity.setMail(personToUpdate.getMail());
        personToUpdateEntity.setPhoneNumber(personToUpdate.getPhoneNumber());

        try {
            personRepository.save(personToUpdateEntity);
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistsException("Ya existe este correo electronico para otra persona");
        }
    }

}