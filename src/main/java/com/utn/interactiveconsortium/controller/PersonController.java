package com.utn.interactiveconsortium.controller;


import com.utn.interactiveconsortium.dto.PersonDto;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping(value = "persons")
@RequiredArgsConstructor
public class PersonController {
    private final PersonService personService;

    @GetMapping
    public Page<PersonDto> getPersons(Pageable page) {
        return personService.getPersons(page);
    }

    @GetMapping(value = "filtersBy")
    public Page<PersonDto> getPerson(@RequestParam(required = false) String name,
                                               @RequestParam(required = false) String lastName,
                                               @RequestParam(required = false) String mail,
                                               @RequestParam(required = false) String dni,
                                               Pageable page){
        return personService.getPerson(name, lastName, mail, dni, page);
    }

    @PostMapping
    public PersonDto createPerson(@RequestBody PersonDto personDto) throws EntityAlreadyExistsException {
        return personService.createPerson(personDto);
    }

    @PutMapping
    public void updatePerson(@RequestBody PersonDto personUpdateDto) throws EntityNotFoundException, EntityAlreadyExistsException {
        personService.updatePerson(personUpdateDto);
    }

}



