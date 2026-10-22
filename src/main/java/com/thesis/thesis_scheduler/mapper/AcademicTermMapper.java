package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.AcademicTermRequest;
import com.thesis.thesis_scheduler.dto.AcademicTermResponse;
import com.thesis.thesis_scheduler.entity.AcademicTerm;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AcademicTermMapper {

    AcademicTerm toEntity(AcademicTermRequest request);

    AcademicTermResponse toResponse(AcademicTerm entity);
}