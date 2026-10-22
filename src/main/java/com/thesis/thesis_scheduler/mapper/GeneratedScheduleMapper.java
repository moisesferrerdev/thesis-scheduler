package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.GeneratedScheduleRequest;
import com.thesis.thesis_scheduler.dto.GeneratedScheduleResponse;
import com.thesis.thesis_scheduler.entity.GeneratedSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GeneratedScheduleMapper {

    @Mapping(target = "generatedScheduleId", ignore = true)
    @Mapping(target = "optimizationRun", ignore = true)
    @Mapping(target = "fitnessScore", ignore = true)
    @Mapping(target = "hardConstraintViolations", ignore = true)
    @Mapping(target = "softConstraintPenalty", ignore = true)
    @Mapping(target = "feasible", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    GeneratedSchedule toEntity(GeneratedScheduleRequest request);

    @Mapping(
            target = "optimizationRunId",
            source = "optimizationRun.optimizationRunId"
    )
    GeneratedScheduleResponse toResponse(GeneratedSchedule entity);
}