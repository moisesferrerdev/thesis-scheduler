package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.InstructorRequest;
import com.thesis.thesis_scheduler.dto.InstructorResponse;
import com.thesis.thesis_scheduler.entity.Instructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstructorMapper {

    @Mapping(target = "instructorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Instructor toEntity(InstructorRequest request);

    InstructorResponse toResponse(Instructor entity);
}