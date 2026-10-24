package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.ProgramRequest;
import com.thesis.thesis_scheduler.dto.ProgramResponse;
import com.thesis.thesis_scheduler.entity.Program;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProgramMapper {

    @Mapping(target = "programId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            ProgramRequest request,
            @MappingTarget Program entity
    );

    Program toEntity(ProgramRequest request);

    ProgramResponse toResponse(Program entity);
}