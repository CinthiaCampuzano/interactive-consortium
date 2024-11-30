package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.AmenityDto;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "Amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    private final String BASIC_ROLES = "hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_RESIDENT')";

    private final String RESTRICTED_ROLES = "hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_RESIDENT')";

    @GetMapping
    @PreAuthorize(BASIC_ROLES)
    public Page<AmenityDto> getAmenities(@RequestParam Long idConsortium, Pageable page) {
        return amenityService.getAmenities(idConsortium, page);
    }

    @GetMapping (value = "filterBy")
    @PreAuthorize(BASIC_ROLES)
    public Page<AmenityDto> getAmenity(@RequestParam Long idConsortium, @RequestParam(required = false) String name, Pageable page) {
        return amenityService.getAmenity(idConsortium, name, page);
    }

    @PostMapping
    @PreAuthorize(RESTRICTED_ROLES)
    public AmenityDto createAmenity(@RequestBody AmenityDto newAmenity) throws EntityAlreadyExistsException, EntityNotFoundException {
        return amenityService.createAmenity(newAmenity);
    }

    @PutMapping
    @PreAuthorize(RESTRICTED_ROLES)
    public void updateAmenity(@RequestBody AmenityDto amenityToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        amenityService.updateAmenity(amenityToUpdate);
    }

    @DeleteMapping(value = "{idAmenity}")
    @PreAuthorize(RESTRICTED_ROLES)
    public void deleteAmenity(@PathVariable Long idAmenity) throws EntityNotFoundException {
        amenityService.deleteAmenity(idAmenity);
    }


}
