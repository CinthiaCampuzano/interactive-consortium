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


//    SuperAdmin para poder ver todos los administradores
    @GetMapping
    public Page<AdministratorDto> getAdministrators(Pageable page) {
        return administratorService.getAdministrators(page);
    }

    //    SuperAdmin para poder ver todos los administradores por filtro
    @GetMapping(value = "filtersBy")
    public Page<AdministratorDto> getAdministrator( @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String lastName,
                                                    @RequestParam(required = false) String mail,
                                                    @RequestParam(required = false) String dni,
                                                    Pageable page){
        return administratorService.getAdministrator(name, lastName, mail, dni, page);
    }

    //    SuperAdmin para poder ver crear un administrador
    @PostMapping
    public AdministratorDto createAdministrator(@RequestBody AdministratorDto newAdministrator) throws EntityAlreadyExistsException {
        return administratorService.createAdministrator(newAdministrator);

    }

    //    SuperAdmin para poder actualizar un administrador

    @PutMapping
    public void updateAdministrator(@RequestBody AdministratorDto administratorToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        administratorService.updateAdministrator(administratorToUpdate);
    }

    //    SuperAdmin para poder eliminar un administrador
    @DeleteMapping(value = "{idAdministrator}")
    public void deleteAdministrator(@PathVariable Long idAdministrator) throws EntityNotFoundException {
        administratorService.deleteAdministrator(idAdministrator);

    }

}
