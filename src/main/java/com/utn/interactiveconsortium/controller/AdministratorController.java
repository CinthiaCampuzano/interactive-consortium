package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.AdministratorDto;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.AdministratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    @GetMapping
    public List<AdministratorDto> getAdministrators() { return administratorService.getAdministrators();}

    @GetMapping (value = "/{nameAdministrator}")
    public List<AdministratorDto> getAdministrator(@PathVariable String nameAdministrator){ return administratorService.getAdministrator(nameAdministrator);}

    @PostMapping
    public  AdministratorDto createAdministrator(@RequestBody AdministratorDto newAdministrator) throws EntityAlreadyExistsException
    { return administratorService.createAdministrator(newAdministrator);

    }

    @PutMapping
    public void updateAdministrator(@RequestBody AdministratorDto administratorToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException
    {
        administratorService.updateAdministrator(administratorToUpdate);
    }

    @DeleteMapping(value = "{idAdministrator}")
    public void deleteAdministrator(@PathVariable Long idAdministrator) throws EntityNotFoundException{
        administratorService.deleteAdministrator(idAdministrator);

    }

}
