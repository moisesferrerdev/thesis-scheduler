package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.CurriculumRequest;
import com.thesis.thesis_scheduler.dto.CurriculumResponse;
import com.thesis.thesis_scheduler.entity.Curriculum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CurriculumMapper {

    @Mapping(target = "curriculumId", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Curriculum toEntity(CurriculumRequest request);

    @Mapping(target = "programId", source = "program.programId")
    @Mapping(target = "programCode", source = "program.programCode")
    @Mapping(target = "programName", source = "program.programName")
    @Mapping(target = "subjectId", source = "subject.subjectId")
    @Mapping(target = "subjectCode", source = "subject.subjectCode")
    @Mapping(target = "subjectName", source = "subject.subjectName")
    CurriculumResponse toResponse(Curriculum entity);

    @Mapping(target = "curriculumId", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(
            CurriculumRequest request,
            @MappingTarget Curriculum entity
    );
}