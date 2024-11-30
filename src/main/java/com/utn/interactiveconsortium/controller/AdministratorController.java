package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.AdministratorDto;

import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.AdministratorService;
import com.utn.interactiveconsortium.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(value = "administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    public Page<AdministratorDto> getAdministrators(Pageable page) {
        return administratorService.getAdministrators(page);
    }

    @GetMapping(value = "filtersBy")
    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    public Page<AdministratorDto> getAdministrator( @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String lastName,
                                                    @RequestParam(required = false) String mail,
                                                    @RequestParam(required = false) String dni,
                                                    Pageable page){
        return administratorService.getAdministrator(name, lastName, mail, dni, page);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    public AdministratorDto createAdministrator(@RequestBody AdministratorDto newAdministrator) throws EntityAlreadyExistsException {
        return administratorService.createAdministrator(newAdministrator);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    public void updateAdministrator(@RequestBody AdministratorDto administratorToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        administratorService.updateAdministrator(administratorToUpdate);
    }

    @DeleteMapping(value = "{idAdministrator}")
    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    public void deleteAdministrator(@PathVariable Long idAdministrator) throws EntityNotFoundException {
        administratorService.deleteAdministrator(idAdministrator);

    }

}
