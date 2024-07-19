package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.dto.ConsortiumDto;
import com.utn.interactiveconsortium.entity.AdministratorEntity;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.PersonEntity;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.ConsortiumMapper;
import com.utn.interactiveconsortium.repository.AdministratorRepository;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;
import com.utn.interactiveconsortium.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsortiumService {
    private final ConsortiumRepository consortiumRepository;
    private final AdministratorRepository administratorRepository;
    private final PersonRepository personRepository;
    private final ConsortiumMapper consortiumMapper;

    public Page<ConsortiumDto> getConsortiums(Pageable page){
        return consortiumMapper.toPage(consortiumRepository.findAll(page));
    }

    public Page<ConsortiumDto> getConsortium(String name, String city, String province, Pageable page){
        Page<ConsortiumEntity> consortiumEntityPage = consortiumRepository.findAdministratorsByFilters(name,city, province, page);

        return consortiumMapper.toPage(consortiumEntityPage);
    }

    public ConsortiumDto createConsortium(ConsortiumDto newConsortium) throws EntityNotFoundException {

        ConsortiumEntity newConsortiumEntity = consortiumMapper.convertDtoToEntity(newConsortium);

        AdministratorEntity administrator = administratorRepository.findById(newConsortium.getAdministrator().getAdministratorId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el administrador"));

        newConsortiumEntity.setAdministrator(administrator);

        consortiumRepository.save(newConsortiumEntity);

        ConsortiumDto newConsortiumDto = consortiumMapper.convertEntityToDto(newConsortiumEntity);

        return newConsortiumDto;
    }

    public void updateConsortium(ConsortiumDto consortiumToUpdate) throws EntityNotFoundException {

        boolean consortiumExists = consortiumRepository.existsById(consortiumToUpdate.getConsortiumId());

        if (!consortiumExists) {
            throw new EntityNotFoundException("No existe este consorcio");
        }

        ConsortiumEntity consortiumToUpdateEntity = consortiumRepository.findById(consortiumToUpdate.getConsortiumId()).get();

        consortiumToUpdateEntity.setName(consortiumToUpdate.getName());
        consortiumToUpdateEntity.setAddress(consortiumToUpdate.getAddress());
        consortiumToUpdateEntity.setCity(consortiumToUpdate.getCity());
        consortiumToUpdateEntity.setProvince(consortiumToUpdate.getProvince());
        consortiumToUpdateEntity.setAdministrator(administratorRepository.findById(consortiumToUpdate.getAdministrator().getAdministratorId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el administrador")));

        consortiumRepository.save(consortiumToUpdateEntity);
    }

    public void deleteConsortium(Long idConsortium) throws EntityNotFoundException{
        boolean consortiumExists = consortiumRepository.existsById(idConsortium);

        if(!consortiumExists) {
            throw new EntityNotFoundException("No se encontro el consorcio");
        }
        consortiumRepository.deleteById(idConsortium);
    }

    public void addConsortiumAndPerson(Long idConsortium, Long idPerson) throws EntityNotFoundException {
        ConsortiumEntity consortium = consortiumRepository.findById(idConsortium)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el consorcio"));

        PersonEntity person = personRepository.findById(idPerson)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro la persona"));

        consortium.getPersons().add(person);
        consortiumRepository.save(consortium);

    }
}
