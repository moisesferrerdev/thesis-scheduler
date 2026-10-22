package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.InstructorAvailabilityRequest;
import com.thesis.thesis_scheduler.dto.InstructorAvailabilityResponse;
import com.thesis.thesis_scheduler.entity.InstructorAvailability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstructorAvailabilityMapper {

    @Mapping(target = "availabilityId", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "term", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    InstructorAvailability toEntity(
            InstructorAvailabilityRequest request
    );

    @Mapping(target = "instructorId", source = "instructor.instructorId")
    @Mapping(target = "employeeNo", source = "instructor.employeeNo")
    @Mapping(
            target = "instructorName",
            expression = "java(entity.getInstructor().getFirstName() + \" \" + entity.getInstructor().getLastName())"
    )
    @Mapping(target = "termId", source = "term.termId")
    @Mapping(target = "termName", source = "term.termName")
    InstructorAvailabilityResponse toResponse(
            InstructorAvailability entity
    );
}