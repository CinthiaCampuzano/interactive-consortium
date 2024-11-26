package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.ConsortiumDto;
import com.utn.interactiveconsortium.dto.PersonDto;
import com.utn.interactiveconsortium.entity.PersonEntity;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.ConsortiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "consortiums")
@RequiredArgsConstructor
public class ConsortiumController {
    private final ConsortiumService consortiumService;

//    El Superadmini puede ver todos los consorcios
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public Page<ConsortiumDto> getConsortiums(Pageable page) {
        return consortiumService.getConsortiums(page);
    }

//    El Superadmin puede obtener todos los consorcios por filtro

    @GetMapping(value = "filterBy")
    public Page<ConsortiumDto> getConsortium(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) String city,
                                             @RequestParam(required = false) String province,
                                             @RequestParam(required = false) String adminName, Pageable page)
    {
        return consortiumService.getConsortium(name, city, province,adminName, page);
    }


//    El administrador puede obtener todos sus consorcios por filtro
    @GetMapping(value = "{idAdministrator}/filter")
    public Page<ConsortiumDto> getConsortiumByAdministratorAndFilters(@PathVariable Long idAdministrator,
                                                                      @RequestParam(required = false) String name,
                                                                      @RequestParam(required = false) String city,
                                                                      @RequestParam(required = false) String province,
                                                                      Pageable page) {
        return consortiumService.getConsortiumByAdministratorAndFilters(idAdministrator, name, city, province, page);
    }

//    El administrador puede obtener todos sus consorcios
    @GetMapping(value = "{idAdministrator}")
    public Page<ConsortiumDto> getConsortiumByAdministrator(@PathVariable Long idAdministrator, Pageable page) {
        return consortiumService.getConsortiumByAdministrator(idAdministrator, page);
    }

//    Este endpoint es para obtener un consorcio por id y poder ver sus detalles.
    @GetMapping("/consortium/{idConsortium}")
    public ConsortiumDto getConsortiumById(@PathVariable Long idConsortium) throws EntityNotFoundException {
        return consortiumService.getConsortiumById(idConsortium);
    }

//    El administrador puede obtener las personas de un consorcio por id de consorcio
    @GetMapping("/{idConsortium}/persons")
    public List<PersonDto> getPersonsByConsortium(@PathVariable Long idConsortium) throws EntityNotFoundException {
        return consortiumService.getPersonsByConsortium(idConsortium);
    }

//    El SuperAdmin puede crear un consorcio
    @PostMapping
    public ConsortiumDto createConsortium(@RequestBody ConsortiumDto newConsortium) throws EntityNotFoundException {
        return consortiumService.createConsortium(newConsortium);

    }

//    Administrador. Este endpoint es para agregar la relacion entre una persona ya sea residente o propietario y un consorcio.
    @PostMapping(value = "consortiumPerson")
    public void addConsortiumAndPerson(@RequestParam Long idConsortium, @RequestParam Long idPerson) throws EntityNotFoundException {
        consortiumService.addConsortiumAndPerson(idConsortium, idPerson);
    }

//    El SuperAdmin puede actualizar un consorcio
    @PutMapping
    public void updateConsortium(@RequestBody ConsortiumDto consortiumToUpdate) throws EntityNotFoundException {
        consortiumService.updateConsortium(consortiumToUpdate);
    }

//    El SuperAdmin puede eliminar un consorcio
    @DeleteMapping(value = "{idConsortium}")
    public void deleteConsortium(@PathVariable Long idConsortium) throws EntityNotFoundException {
        consortiumService.deleteConsortium(idConsortium);

    }

//    El administrador puede eliminar la relacion entre una persona y un consorcio
    @DeleteMapping("/{idConsortium}/persons/{idPerson}")
    public void deletePersonFromConsortium(@PathVariable Long idConsortium, @PathVariable Long idPerson) throws EntityNotFoundException {
        consortiumService.deletePersonFromConsortium(idConsortium, idPerson);
    }

}
