package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.InstructorQualificationRequest;
import com.thesis.thesis_scheduler.dto.InstructorQualificationResponse;
import com.thesis.thesis_scheduler.entity.InstructorQualification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstructorQualificationMapper {

    @Mapping(target = "qualificationId", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "approved", source = "approved")
    InstructorQualification toEntity(
            InstructorQualificationRequest request
    );

    @Mapping(target = "instructorId", source = "instructor.instructorId")
    @Mapping(target = "employeeNo", source = "instructor.employeeNo")
    @Mapping(
            target = "instructorName",
            expression = "java(entity.getInstructor().getFirstName() + \" \" + entity.getInstructor().getLastName())"
    )
    @Mapping(target = "subjectId", source = "subject.subjectId")
    @Mapping(target = "subjectCode", source = "subject.subjectCode")
    @Mapping(target = "subjectName", source = "subject.subjectName")
    @Mapping(target = "approved", source = "approved")
    InstructorQualificationResponse toResponse(
            InstructorQualification entity
    );
}