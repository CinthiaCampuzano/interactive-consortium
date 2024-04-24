package com.utn.interactiveconsortium.controller;

import com.utn.interactiveconsortium.dto.ConsortiumDto;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.ConsortiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "consortiums")
@RequiredArgsConstructor
public class ConsortiumController {
    private final ConsortiumService consortiumService;

    @GetMapping
    public Page<ConsortiumDto> getConsortiums(Pageable page) {
        return consortiumService.getConsortiums(page);
    }

    @GetMapping(value = "filterBy")
    public Page<ConsortiumDto> getConsortium(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) String city,
                                             @RequestParam(required = false) String province,Pageable page){
        return consortiumService.getConsortium(name, city, province, page);
    }

    @PostMapping
    public ConsortiumDto createConsortium(@RequestBody ConsortiumDto newConsortium) {
        return consortiumService.createConsortium(newConsortium);

    }

    @PutMapping
    public void updateConsortium(@RequestBody ConsortiumDto consortiumToUpdate) throws EntityNotFoundException {
        consortiumService.updateConsortium(consortiumToUpdate);
    }

    @DeleteMapping(value = "{idConsortium}")
    public void deleteConsortium(@PathVariable Long idConsortium) throws EntityNotFoundException {
        consortiumService.deleteConsortium(idConsortium);

    }

}
