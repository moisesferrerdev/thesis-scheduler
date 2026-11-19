package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.SubjectRequest;
import com.thesis.thesis_scheduler.dto.SubjectResponse;
import com.thesis.thesis_scheduler.entity.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    @Mapping(
            target = "requiredLaboratoryClassification",
            ignore = true
    )
    Subject toEntity(SubjectRequest request);

    @Mapping(
            target = "requiredLaboratoryClassificationId",
            source = "requiredLaboratoryClassification.classificationId"
    )
    @Mapping(
            target = "requiredLaboratoryClassificationName",
            source = "requiredLaboratoryClassification.classificationName"
    )
    SubjectResponse toResponse(Subject entity);

    @Mapping(target = "subjectId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(
            target = "requiredLaboratoryClassification",
            ignore = true
    )
    void updateEntity(
            SubjectRequest request,
            @MappingTarget Subject entity
    );
}