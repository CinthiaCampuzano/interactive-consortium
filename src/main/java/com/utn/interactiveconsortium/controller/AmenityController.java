package com.utn.interactiveconsortium.controller;

import java.io.IOException;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.utn.interactiveconsortium.dto.AmenityDto;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.AmenityService;

import lombok.RequiredArgsConstructor;

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
    public AmenityDto createAmenity(@RequestBody @Valid AmenityDto newAmenity) throws EntityAlreadyExistsException, EntityNotFoundException {
        return amenityService.createAmenity(newAmenity);
    }

    @PutMapping
    @PreAuthorize(RESTRICTED_ROLES)
    public void updateAmenity(@RequestBody @Valid AmenityDto amenityToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        amenityService.updateAmenity(amenityToUpdate);
    }

    @DeleteMapping(value = "{idAmenity}")
    @PreAuthorize(RESTRICTED_ROLES)
    public void deleteAmenity(@PathVariable Long idAmenity) throws EntityNotFoundException {
        amenityService.deleteAmenity(idAmenity);
    }

    @PostMapping("/{amenityId}/upload")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public AmenityDto uploadImage(
            @PathVariable Long amenityId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws EntityNotFoundException, IOException {
        return amenityService.uploadImage(amenityId, file);
    }

    @GetMapping("/{amenityId}/download")
    @PreAuthorize(BASIC_ROLES)
    public void downloadImage(
            @PathVariable Long amenityId,
            HttpServletResponse response
    ) throws EntityNotFoundException, MessagingException, IOException {
        amenityService.downloadImage(amenityId, response);
    }


}
