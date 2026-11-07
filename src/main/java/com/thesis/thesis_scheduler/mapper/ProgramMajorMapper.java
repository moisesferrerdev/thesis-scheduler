
package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.ProgramMajorRequest;
import com.thesis.thesis_scheduler.dto.ProgramMajorResponse;
import com.thesis.thesis_scheduler.entity.ProgramMajor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProgramMajorMapper {

    @Mapping(target = "programMajorId", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProgramMajor toEntity(ProgramMajorRequest request);

    @Mapping(target = "programId", source = "program.programId")
    @Mapping(target = "programCode", source = "program.programCode")
    @Mapping(target = "programName", source = "program.programName")
    ProgramMajorResponse toResponse(ProgramMajor entity);

    @Mapping(target = "programMajorId", ignore = true)
    @Mapping(target = "program", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            ProgramMajorRequest request,
            @MappingTarget ProgramMajor entity
    );
}
