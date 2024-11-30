package com.utn.interactiveconsortium.controller;


import com.utn.interactiveconsortium.dto.PersonDto;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping(value = "persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    //TODO Administrador puede obtener todas las personas de un consorcio- Modificar este endpoint para que reciba el id del consorcio
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public Page<PersonDto> getPersons(Pageable page) {
        return personService.getPersons(page);
    }

    //TODO Modificar este endpoint para que reciba el id del consorcio
    @GetMapping(value = "filtersBy")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public Page<PersonDto> getPerson(@RequestParam(required = false) String name,
                                               @RequestParam(required = false) String lastName,
                                               @RequestParam(required = false) String mail,
                                               @RequestParam(required = false) String dni,
                                               Pageable page){
        return personService.getPerson(name, lastName, mail, dni, page);
    }

    @GetMapping (value = "filterByDni")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public PersonDto getPersonByDni(@RequestParam(required = false) String dni) throws EntityNotFoundException {
        return personService.getPersonByDni(dni);
    }

    @GetMapping("/consortium/{consortiumId}/owners")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public List<PersonDto> getOwnersByConsortium(@PathVariable Long consortiumId) throws EntityNotFoundException {
        return personService.getOwnersByConsortium(consortiumId);
    }

    @GetMapping("/consortium/{consortiumId}/residents")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public List<PersonDto> getResidentsByConsortium(@PathVariable Long consortiumId) throws EntityNotFoundException {
        return personService.getResidentsByConsortium(consortiumId);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public PersonDto createPerson(@RequestBody PersonDto personDto) throws EntityAlreadyExistsException {
        return personService.createPerson(personDto);
    }

    //TODO esto tiene que tener un control
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public void updatePerson(@RequestBody PersonDto personUpdateDto) throws EntityNotFoundException, EntityAlreadyExistsException {
        personService.updatePerson(personUpdateDto);
    }

    //TODO esto tiene que tener un control
    @DeleteMapping(value = "{idPerson}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public void deletePerson(@PathVariable Long idPerson) throws EntityNotFoundException {
        personService.deletePerson(idPerson);
    }

}



