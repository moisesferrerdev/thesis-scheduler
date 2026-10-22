package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.TeachingAssignmentRequest;
import com.thesis.thesis_scheduler.dto.TeachingAssignmentResponse;
import com.thesis.thesis_scheduler.entity.TeachingAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeachingAssignmentMapper {

    @Mapping(target = "assignmentId", ignore = true)
    @Mapping(target = "sectionSubject", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TeachingAssignment toEntity(TeachingAssignmentRequest request);

    @Mapping(
            target = "sectionSubjectId",
            source = "sectionSubject.sectionSubjectId"
    )
    @Mapping(
            target = "sectionCode",
            source = "sectionSubject.section.sectionCode"
    )
    @Mapping(
            target = "offeringCode",
            source = "sectionSubject.offering.offeringCode"
    )
    @Mapping(target = "instructorId", source = "instructor.instructorId")
    @Mapping(target = "employeeNo", source = "instructor.employeeNo")
    @Mapping(
            target = "instructorName",
            expression = "java(entity.getInstructor().getFirstName() + \" \" + entity.getInstructor().getLastName())"
    )
    TeachingAssignmentResponse toResponse(TeachingAssignment entity);
}