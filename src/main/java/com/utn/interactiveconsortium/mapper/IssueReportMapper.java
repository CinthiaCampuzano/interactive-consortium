package com.utn.interactiveconsortium.mapper;


import com.utn.interactiveconsortium.dto.IssueReportDto;
import com.utn.interactiveconsortium.entity.IssueReportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {ConsortiumMapper.class})
public interface IssueReportMapper {

    IssueReportDto convertEntityToDto(IssueReportEntity issueReportEntity);

    @Mapping(target = "consortium", ignore = true)
    IssueReportEntity convertDtoToEntity(IssueReportDto issueReportDto);

    default Page<IssueReportDto> toPage(Page<IssueReportEntity> page){
        return page.map(this::convertEntityToDto);
    }

}
