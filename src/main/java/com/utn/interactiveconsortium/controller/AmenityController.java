package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.AmenityDto;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "Amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

//    El Administrador puede ver todos los amenities por consorcio
//    El Residente puede ver todos los amenities por consorcio
    @GetMapping
    public Page<AmenityDto> getAmenities(@RequestParam Long idConsortium, Pageable page) {
        return amenityService.getAmenities(idConsortium, page);
    }

//    El Administrador puede ver todos los amenities por consorcio por filtro
//    El Residente puede ver todos los amenities por consorcio por filtro
    @GetMapping (value = "filterBy")
    public Page<AmenityDto> getAmenity(@RequestParam Long idConsortium, @RequestParam(required = false) String name, Pageable page) {
        return amenityService.getAmenity(idConsortium, name, page);
    }

//    El Administrador puede crear un amenity
    @PostMapping
    public AmenityDto createAmenity(@RequestBody AmenityDto newAmenity) throws EntityAlreadyExistsException, EntityNotFoundException {
        return amenityService.createAmenity(newAmenity);
    }

//    El Administrador puede actualizar un amenity
    @PutMapping
    public void updateAmenity(@RequestBody AmenityDto amenityToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        amenityService.updateAmenity(amenityToUpdate);
    }

//    El Administrador puede eliminar un amenity
    @DeleteMapping(value = "{idAmenity}")
    public void deleteAmenity(@PathVariable Long idAmenity) throws EntityNotFoundException {
        amenityService.deleteAmenity(idAmenity);
    }


}
