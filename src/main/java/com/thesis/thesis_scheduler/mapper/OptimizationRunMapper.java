package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.OptimizationRunRequest;
import com.thesis.thesis_scheduler.dto.OptimizationRunResponse;
import com.thesis.thesis_scheduler.entity.OptimizationRun;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OptimizationRunMapper {

    @Mapping(target = "optimizationRunId", ignore = true)
    @Mapping(target = "term", ignore = true)
    @Mapping(target = "runStatus", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OptimizationRun toEntity(OptimizationRunRequest request);

    @Mapping(target = "termId", source = "term.termId")
    @Mapping(target = "termName", source = "term.termName")
    OptimizationRunResponse toResponse(OptimizationRun entity);

    @Mapping(target = "optimizationRunId", ignore = true)
    @Mapping(target = "term", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    void updateEntity(
            OptimizationRunRequest request,
            @MappingTarget OptimizationRun entity
    );
}