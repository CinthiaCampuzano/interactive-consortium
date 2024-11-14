package com.utn.interactiveconsortium.controller;


import com.utn.interactiveconsortium.dto.PersonDto;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping(value = "persons")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

// Administrador puede obtener todas las personas de un consorcio- Modificar este endpoint para que reciba el id del consorcio
    @GetMapping
    public Page<PersonDto> getPersons(Pageable page) {
        return personService.getPersons(page);
    }

//    Modificar este endpoint para que reciba el id del consorcio
    @GetMapping(value = "filtersBy")
    public Page<PersonDto> getPerson(@RequestParam(required = false) String name,
                                               @RequestParam(required = false) String lastName,
                                               @RequestParam(required = false) String mail,
                                               @RequestParam(required = false) String dni,
                                               Pageable page){
        return personService.getPerson(name, lastName, mail, dni, page);
    }

//    Administrador obtiene una persona por su dni
    @GetMapping (value = "filterByDni")
    public PersonDto getPersonByDni(@RequestParam(required = false) String dni) throws EntityNotFoundException {
        return personService.getPersonByDni(dni);
    }

//    Administrador obtiene una lista de Propietarios de un consorcio
    @GetMapping("/consortium/{consortiumId}/owners")
    public List<PersonDto> getOwnersByConsortium(@PathVariable Long consortiumId) throws EntityNotFoundException {
        return personService.getOwnersByConsortium(consortiumId);
    }

//    Administrador obtiene una lista de Residentes de un consorcio
    @GetMapping("/consortium/{consortiumId}/residents")
    public List<PersonDto> getResidentsByConsortium(@PathVariable Long consortiumId) throws EntityNotFoundException {
        return personService.getResidentsByConsortium(consortiumId);
    }

//    Administrador crea una persona
    @PostMapping
    public PersonDto createPerson(@RequestBody PersonDto personDto) throws EntityAlreadyExistsException {
        return personService.createPerson(personDto);
    }

//    Administrador actualiza una persona

    @PutMapping
    public void updatePerson(@RequestBody PersonDto personUpdateDto) throws EntityNotFoundException, EntityAlreadyExistsException {
        personService.updatePerson(personUpdateDto);
    }

//    Administrador elimina una persona
    @DeleteMapping(value = "{idPerson}")
    public void deletePerson(@PathVariable Long idPerson) throws EntityNotFoundException {
        personService.deletePerson(idPerson);
    }

}



