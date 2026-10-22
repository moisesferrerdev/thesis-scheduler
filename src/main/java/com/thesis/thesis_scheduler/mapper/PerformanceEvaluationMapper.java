package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.PerformanceEvaluationRequest;
import com.thesis.thesis_scheduler.dto.PerformanceEvaluationResponse;
import com.thesis.thesis_scheduler.entity.PerformanceEvaluation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PerformanceEvaluationMapper {

    @Mapping(target = "evaluationId", ignore = true)
    @Mapping(target = "generatedSchedule", ignore = true)
    @Mapping(target = "scheduleVersion", ignore = true)
    @Mapping(target = "hardConstraintScore", ignore = true)
    @Mapping(target = "softConstraintScore", ignore = true)
    @Mapping(target = "fitnessScore", ignore = true)
    @Mapping(target = "evaluatedAt", ignore = true)
    PerformanceEvaluation toEntity(
            PerformanceEvaluationRequest request
    );

    @Mapping(
            target = "generatedScheduleId",
            source = "generatedSchedule.generatedScheduleId"
    )
    @Mapping(
            target = "scheduleVersionId",
            source = "scheduleVersion.scheduleVersionId"
    )
    PerformanceEvaluationResponse toResponse(
            PerformanceEvaluation entity
    );
}