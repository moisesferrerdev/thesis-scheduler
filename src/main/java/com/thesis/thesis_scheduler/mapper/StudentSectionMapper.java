package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.StudentSectionRequest;
import com.thesis.thesis_scheduler.dto.StudentSectionResponse;
import com.thesis.thesis_scheduler.entity.StudentSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentSectionMapper {

    @Mapping(target = "sectionId", ignore = true)
    @Mapping(target = "academicTerm", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StudentSection toEntity(StudentSectionRequest request);

    @Mapping(target = "termId", source = "academicTerm.termId")
    @Mapping(target = "termName", source = "academicTerm.termName")
    @Mapping(target = "programId", source = "program.programId")
    @Mapping(target = "programCode", source = "program.programCode")
    @Mapping(target = "programName", source = "program.programName")
    StudentSectionResponse toResponse(StudentSection entity);
}