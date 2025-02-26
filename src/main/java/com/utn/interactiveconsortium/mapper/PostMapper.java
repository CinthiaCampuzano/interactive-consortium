package com.utn.interactiveconsortium.mapper;


import com.utn.interactiveconsortium.dto.PostDto;
import com.utn.interactiveconsortium.entity.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
@Mapper(componentModel = "spring", uses = {ConsortiumMapper.class})
public interface PostMapper {

    PostDto convertEntityToDto(PostEntity postEntity);

    @Mapping(target = "consortium", ignore = true)
    PostEntity convertDtoToEntity(PostDto postDto);

    default Page<PostDto> toPage(Page<PostEntity> page){
        return page.map(this::convertEntityToDto);
    }
}
