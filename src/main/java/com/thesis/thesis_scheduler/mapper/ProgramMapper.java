package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.ProgramRequest;
import com.thesis.thesis_scheduler.dto.ProgramResponse;
import com.thesis.thesis_scheduler.entity.Program;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgramMapper {

    Program toEntity(ProgramRequest request);

    ProgramResponse toResponse(Program entity);
}