package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.AcademicTermRequest;
import com.thesis.thesis_scheduler.dto.AcademicTermResponse;
import com.thesis.thesis_scheduler.entity.AcademicTerm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AcademicTermMapper {

    AcademicTerm toEntity(AcademicTermRequest request);

    AcademicTermResponse toResponse(AcademicTerm entity);

    @Mapping(target = "termId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            AcademicTermRequest request,
            @MappingTarget AcademicTerm entity
    );
}