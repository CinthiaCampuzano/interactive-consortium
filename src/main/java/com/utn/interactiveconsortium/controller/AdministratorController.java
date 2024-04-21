package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.AdministratorDto;

import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.AdministratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(value = "administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    @GetMapping
    public Page<AdministratorDto> getAdministrators(Pageable page) {
        return administratorService.getAdministrators(page);
    }

    @GetMapping(value = "filtersBy")
    public Page<AdministratorDto> getAdministrator( @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String lastName,
                                                    @RequestParam(required = false) String mail,
                                                    @RequestParam(required = false) String dni,
                                                    Pageable page){
        return administratorService.getAdministrator(name, lastName, mail, dni, page);
    }

    @PostMapping
    public AdministratorDto createAdministrator(@RequestBody AdministratorDto newAdministrator) throws EntityAlreadyExistsException {
        return administratorService.createAdministrator(newAdministrator);

    }

    @PutMapping
    public void updateAdministrator(@RequestBody AdministratorDto administratorToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        administratorService.updateAdministrator(administratorToUpdate);
    }

    @DeleteMapping(value = "{idAdministrator}")
    public void deleteAdministrator(@PathVariable Long idAdministrator) throws EntityNotFoundException {
        administratorService.deleteAdministrator(idAdministrator);

    }

}
