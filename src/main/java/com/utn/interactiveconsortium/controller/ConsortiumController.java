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

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public Page<ConsortiumDto> getConsortiums(Pageable page) {
        return consortiumService.getConsortiums(page);
    }

    @GetMapping(value = "filterBy")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT')")
    public Page<ConsortiumDto> getConsortium(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) String city,
                                             @RequestParam(required = false) String province,
                                             @RequestParam(required = false) String adminName,
                                             Pageable page) {
        return consortiumService.getConsortium(name, city, province,adminName, page);
    }


    @GetMapping(value = "/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public Page<ConsortiumDto> getConsortiumByAdministratorAndFilters(@RequestParam(required = false) String name,
                                                                      @RequestParam(required = false) String city,
                                                                      @RequestParam(required = false) String province,
                                                                      Pageable page) {
        return consortiumService.getConsortiumByAdministratorAndFilters(name, city, province, page);
    }

    @GetMapping("/administrator")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public Page<ConsortiumDto> getConsortiumByAdministrator(Pageable page) {
        //TODO aca sacar directamente del usuario loggeado
        return consortiumService.getConsortiumByAdministrator(page);
    }

    @GetMapping("/consortium/{idConsortium}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public ConsortiumDto getConsortiumById(@PathVariable Long idConsortium) throws EntityNotFoundException {
        return consortiumService.getConsortiumById(idConsortium);
    }

    @GetMapping("/{idConsortium}/persons")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public List<PersonDto> getPersonsByConsortium(@PathVariable Long idConsortium) throws EntityNotFoundException {
        return consortiumService.getPersonsByConsortium(idConsortium);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    public ConsortiumDto createConsortium(@RequestBody ConsortiumDto newConsortium) throws EntityNotFoundException {
        return consortiumService.createConsortium(newConsortium);

    }

    @PostMapping(value = "consortiumPerson")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public void addConsortiumAndPerson(@RequestParam Long idConsortium, @RequestParam Long idPerson) throws EntityNotFoundException {
        consortiumService.addConsortiumAndPerson(idConsortium, idPerson);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    public void updateConsortium(@RequestBody ConsortiumDto consortiumToUpdate) throws EntityNotFoundException {
        consortiumService.updateConsortium(consortiumToUpdate);
    }

    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    @DeleteMapping(value = "{idConsortium}")
    public void deleteConsortium(@PathVariable Long idConsortium) throws EntityNotFoundException {
        consortiumService.deleteConsortium(idConsortium);

    }

    @DeleteMapping("/{idConsortium}/persons/{idPerson}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public void deletePersonFromConsortium(@PathVariable Long idConsortium, @PathVariable Long idPerson) throws EntityNotFoundException {
        consortiumService.deletePersonFromConsortium(idConsortium, idPerson);
    }

}
