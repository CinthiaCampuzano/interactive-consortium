package com.utn.interactiveconsortium.service;

import com.utn.interactiveconsortium.config.MinioConfig;
import com.utn.interactiveconsortium.dto.AmenityDto;
import com.utn.interactiveconsortium.dto.ConsortiumDto;
import com.utn.interactiveconsortium.entity.AmenityEntity;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.exception.EntityAlreadyExistsException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.mapper.AmenityMapper;
import com.utn.interactiveconsortium.repository.AmenityRepository;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;
import com.utn.interactiveconsortium.util.MinioUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;

    private final ConsortiumRepository consortiumRepository;

    private final AmenityMapper amenityMapper;

    private final MinioConfig minioConfig;

    private final MinioUtils minioUtils;

    public Page<AmenityDto> getAmenities(Long idConsortium, Pageable page){
        return amenityMapper.toPage(amenityRepository.findByConsortiumConsortiumId(idConsortium, page));
    }

    public Page<AmenityDto> getAmenity(Long idConsortium, String name, Pageable page){
        Page<AmenityEntity> amenityEntityPage = amenityRepository.findAmenitiesByNameAndConsortiumId(idConsortium, name, page);

        return amenityMapper.toPage(amenityEntityPage);
    }

    public AmenityDto createAmenity(AmenityDto newAmenity) throws EntityAlreadyExistsException, EntityNotFoundException {

        boolean amenityExists = amenityRepository.existsByNameAndConsortiumId(newAmenity.getName(), newAmenity.getConsortium().getConsortiumId());

        if (amenityExists){
            throw new EntityAlreadyExistsException("Ya existe un espacio comun con ese nombre en este consorcio");
        }

        AmenityEntity newAmenityEntity = amenityMapper.convertDtoToEntity(newAmenity);

        ConsortiumEntity consortium = consortiumRepository.findById(newAmenity.getConsortium().getConsortiumId())
                .orElseThrow(() -> new EntityNotFoundException("Consorcio no encontrado"));

        newAmenityEntity.setConsortium(consortium);
        amenityRepository.save(newAmenityEntity);

        AmenityDto newAmenityDto = amenityMapper.convertEntityToDto(newAmenityEntity);

        return newAmenityDto;
    }

    public void updateAmenity(AmenityDto amenityToUpdate) throws EntityNotFoundException, EntityAlreadyExistsException {
        boolean amenityExists = amenityRepository.existsById(amenityToUpdate.getAmenityId());

        if (!amenityExists) {
            throw new EntityNotFoundException("No existe ese espacio comun");
        }

        AmenityEntity amenityToUpdateEntity = amenityRepository.findById(amenityToUpdate.getAmenityId()).get();

        boolean sameAmenity = amenityToUpdateEntity.getName().equals(amenityToUpdate.getName()) &&
                amenityToUpdateEntity.getConsortium().getConsortiumId().equals(amenityToUpdate.getConsortium().getConsortiumId());

        if (!sameAmenity) {
            boolean amenityExistsToUpdate = amenityRepository.existsByNameAndConsortiumId
                    (amenityToUpdate.getName(), amenityToUpdate.getConsortium().getConsortiumId());
            if (amenityExistsToUpdate) {
                throw new EntityAlreadyExistsException("Ya existe un espacio comun con ese nombre en este consorcio");
            }
        }

        ConsortiumEntity consortium = consortiumRepository.findById(amenityToUpdate.getConsortium().getConsortiumId())
                .orElseThrow(() -> new EntityNotFoundException("Consorcio no encontrado"));

        amenityToUpdateEntity.setName(amenityToUpdate.getName());
        amenityToUpdateEntity.setMaxBookings(amenityToUpdate.getMaxBookings());
        amenityToUpdateEntity.setCostOfUse(amenityToUpdate.getCostOfUse());


        amenityRepository.save(amenityToUpdateEntity);
    }

    public void deleteAmenity(Long idAmenity) throws EntityNotFoundException{
        boolean amenityExists = amenityRepository.existsById(idAmenity);

        if(!amenityExists) {
            throw new EntityNotFoundException("No se encontro el espacio comun");
        }
        amenityRepository.deleteById(idAmenity);
    }


    public AmenityDto uploadImage(Long amenityId, MultipartFile file) throws EntityNotFoundException, IOException {
        AmenityEntity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el espacio comun"));

        String filePath = "amenityId/" + amenityId + "/" + file.getOriginalFilename();

        minioUtils.uploadFile(minioConfig.getBucketName(), file, filePath, file.getContentType());

        amenity.setImagePath(filePath);
        amenityRepository.save(amenity);

        return amenityMapper.convertEntityToDto(amenity);
    }

    public void downloadImage(Long amenityId, HttpServletResponse response) throws EntityNotFoundException, IOException {
        AmenityEntity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el espacio comun"));

        String filePath = amenity.getImagePath();
        if (filePath == null || filePath.isEmpty()) {
            throw new EntityNotFoundException("No se encontró la imagen para el espacio comun");
        }

        InputStream fileInputStream = minioUtils.getObject(minioConfig.getBucketName(), filePath);
        response.setHeader("Content-Disposition", "attachment;filename=" + filePath.substring(filePath.lastIndexOf("/") + 1));
        response.setContentType("image/jpeg");
        response.setCharacterEncoding("UTF-8");
        IOUtils.copy(fileInputStream, response.getOutputStream());
        response.flushBuffer();
    }
}
