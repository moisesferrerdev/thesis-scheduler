package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.SchedulingConstraintRequest;
import com.thesis.thesis_scheduler.dto.SchedulingConstraintResponse;
import com.thesis.thesis_scheduler.entity.SchedulingConstraint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SchedulingConstraintMapper {

    @Mapping(target = "constraintId", ignore = true)
    @Mapping(target = "term", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", source = "active")
    SchedulingConstraint toEntity(
            SchedulingConstraintRequest request
    );

    @Mapping(target = "termId", source = "term.termId")
    @Mapping(target = "termName", source = "term.termName")
    @Mapping(target = "active", source = "active")
    SchedulingConstraintResponse toResponse(
            SchedulingConstraint entity
    );

    @Mapping(target = "constraintId", ignore = true)
    @Mapping(target = "term", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            SchedulingConstraintRequest request,
            @MappingTarget SchedulingConstraint entity
    );
}