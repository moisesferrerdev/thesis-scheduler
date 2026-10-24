package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.SubjectRequest;
import com.thesis.thesis_scheduler.dto.SubjectResponse;
import com.thesis.thesis_scheduler.entity.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    Subject toEntity(SubjectRequest request);

    SubjectResponse toResponse(Subject entity);

    @Mapping(target = "subjectId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            SubjectRequest request,
            @MappingTarget Subject entity
    );
}