package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.SubjectRequest;
import com.thesis.thesis_scheduler.dto.SubjectResponse;
import com.thesis.thesis_scheduler.entity.Subject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    Subject toEntity(SubjectRequest request);

    SubjectResponse toResponse(Subject entity);
}