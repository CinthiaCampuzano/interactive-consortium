package com.utn.interactiveconsortium.controller;

import java.io.IOException;
import java.util.List;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;

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

import com.utn.interactiveconsortium.dto.ConsortiumDto;
import com.utn.interactiveconsortium.dto.PersonDto;
import com.utn.interactiveconsortium.exception.CustomIllegalArgumentException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.ConsortiumService;

import lombok.RequiredArgsConstructor;

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
        return consortiumService.getConsortiumByAdministrator(page);
    }

    @GetMapping("/person")
    @PreAuthorize("hasAnyAuthority('ROLE_RESIDENT', 'ROLE_PROPIETARY')")
    public Page<ConsortiumDto> getConsortiumByPerson(Pageable page) {
        return consortiumService.getConsortiumByPerson(page);
    }

    @GetMapping("/consortium/{idConsortium}")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_RESIDENT', 'ROLE_ADMIN')")
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
    public ConsortiumDto createConsortium(@RequestBody ConsortiumDto newConsortium) throws EntityNotFoundException, CustomIllegalArgumentException {
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

    @PostMapping("/{consortiumId}/upload")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN')")
    public ConsortiumDto uploadImage(
            @PathVariable Long consortiumId,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws EntityNotFoundException, IOException {
        return consortiumService.uploadImage(consortiumId, file);
    }

    @GetMapping("/{consortiumId}/download")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMIN', 'ROLE_RESIDENT', 'ROLE_PROPIETARY')")
    public void downloadImage(
            @PathVariable Long consortiumId,
            HttpServletResponse response
    ) throws EntityNotFoundException, MessagingException, IOException {
        consortiumService.downloadImage(consortiumId, response);;
    }

}