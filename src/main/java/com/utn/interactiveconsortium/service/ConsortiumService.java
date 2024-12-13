package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.config.MinioConfig;
import com.utn.interactiveconsortium.dto.ConsortiumDto;
import com.utn.interactiveconsortium.dto.PersonDto;
import com.utn.interactiveconsortium.entity.AdministratorEntity;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.entity.PersonEntity;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.ConsortiumMapper;
import com.utn.interactiveconsortium.mapper.PersonMapper;
import com.utn.interactiveconsortium.repository.AdministratorRepository;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;
import com.utn.interactiveconsortium.repository.PersonRepository;
import com.utn.interactiveconsortium.util.MinioUtils;
import io.minio.PutObjectArgs;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsortiumService {

    private final LoggedUserService loggedUserService;

    private final ConsortiumRepository consortiumRepository;

    private final AdministratorRepository administratorRepository;

    private final PersonRepository personRepository;

    private final ConsortiumMapper consortiumMapper;

    private final PersonMapper personMapper;

    private final MinioConfig minioConfig;

    private final MinioUtils minioUtils;

    public Page<ConsortiumDto> getConsortiums(Pageable page){
        return consortiumMapper.toPage(consortiumRepository.findAll(page));
    }

    public Page<ConsortiumDto> getConsortium(String name, String city, String province, String adminName, Pageable page){
        Page<ConsortiumEntity> consortiumEntityPage = consortiumRepository.findAdministratorsByFilters(name,city, province,adminName, page);
        return consortiumMapper.toPage(consortiumEntityPage);
    }

    public Page<ConsortiumDto> getConsortiumByAdministrator(Pageable page){
        List<Long> consortiumIds = loggedUserService.getAssociatedConsortiumIds();
        AdministratorEntity administrator = loggedUserService.getLoggedAdministrator();
        Page<ConsortiumEntity> consortiumEntityPage = consortiumRepository
                .findAllAssociatedConsortiums(administrator.getAdministratorId(), consortiumIds, page);
        return consortiumMapper.toPage(consortiumEntityPage);
    }

    public Page<ConsortiumDto> getConsortiumByPerson(Pageable page){
        List<Long> consortiumIds = loggedUserService.getAssociatedConsortiumIds();
        PersonEntity person = loggedUserService.getLoggedPerson();
        Page<ConsortiumEntity> consortiumEntityPage = consortiumRepository
                .findAllAssociatedConsortiumsByPerson(person.getPersonId(), consortiumIds, page);
        return consortiumMapper.toPage(consortiumEntityPage);
    }

    public Page<ConsortiumDto> getConsortiumByAdministratorAndFilters(String name, String city, String province, Pageable page){
        Long administratorId = loggedUserService.getLoggedAdministrator().getAdministratorId();
        Page<ConsortiumEntity> consortiumEntityPage = consortiumRepository.findByAdministratorAndFilters(administratorId, name,city, province, page);

        return consortiumMapper.toPage(consortiumEntityPage);
    }

    public ConsortiumDto getConsortiumById(Long idConsortium) throws EntityNotFoundException {
        ConsortiumEntity consortium = consortiumRepository.findById(idConsortium)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el consorcio"));
        return consortiumMapper.convertEntityToDto(consortium);
    }
    public List<PersonDto> getPersonsByConsortium(Long idConsortium) throws EntityNotFoundException {
        ConsortiumEntity consortium = consortiumRepository.findById(idConsortium)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el consorcio"));
        return consortium.getPersons().stream()
                .map(personMapper::convertEntityToDto)
                .collect(Collectors.toList());
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

    public void deletePersonFromConsortium(Long idConsortium, Long idPerson) throws EntityNotFoundException {
        ConsortiumEntity consortium = consortiumRepository.findById(idConsortium)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el consorcio"));
        PersonEntity person = personRepository.findById(idPerson)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la persona"));

        if (!consortium.getPersons().remove(person)) {
            throw new EntityNotFoundException("La persona no está asociada con el consorcio");
        }

        consortiumRepository.save(consortium);
    }

    public ConsortiumEntity findConsortiumById(Long consortiumId) throws EntityNotFoundException {
        return consortiumRepository.findById(consortiumId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el consorcio"));
    }

    public ConsortiumDto uploadImage(Long consortiumId, MultipartFile file) throws EntityNotFoundException, IOException {
        ConsortiumEntity consortium = consortiumRepository.findById(consortiumId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el consorcio"));

        String filePath = "consortiums/" + consortiumId + "/" + file.getOriginalFilename();

        minioUtils.uploadFile(minioConfig.getBucketName(), file, filePath, file.getContentType());

        consortium.setImagePath(filePath);
        consortiumRepository.save(consortium);

        return consortiumMapper.convertEntityToDto(consortium);
    }

    public void downloadImage(Long consortiumId, HttpServletResponse response) throws EntityNotFoundException, IOException {
        ConsortiumEntity consortium = consortiumRepository.findById(consortiumId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el consorcio"));

        String filePath = consortium.getImagePath();
        if (filePath == null || filePath.isEmpty()) {
            throw new EntityNotFoundException("No se encontró la imagen para el consorcio");
        }

        InputStream fileInputStream = minioUtils.getObject(minioConfig.getBucketName(), filePath);
        response.setHeader("Content-Disposition", "attachment;filename=" + filePath.substring(filePath.lastIndexOf("/") + 1));
        response.setContentType("image/jpeg");
        response.setCharacterEncoding("UTF-8");
        IOUtils.copy(fileInputStream, response.getOutputStream());
        response.flushBuffer();
    }

}
