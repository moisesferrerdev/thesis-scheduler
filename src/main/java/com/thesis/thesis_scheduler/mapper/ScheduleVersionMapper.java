package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.ScheduleVersionRequest;
import com.thesis.thesis_scheduler.dto.ScheduleVersionResponse;
import com.thesis.thesis_scheduler.entity.ScheduleVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleVersionMapper {

    @Mapping(target = "scheduleVersionId", ignore = true)
    @Mapping(target = "generatedSchedule", ignore = true)
    @Mapping(target = "parentVersion", ignore = true)
    @Mapping(target = "versionNumber", ignore = true)
    @Mapping(target = "versionStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ScheduleVersion toEntity(ScheduleVersionRequest request);

    @Mapping(
            target = "generatedScheduleId",
            source = "generatedSchedule.generatedScheduleId"
    )
    @Mapping(
            target = "parentVersionId",
            source = "parentVersion.scheduleVersionId"
    )
    ScheduleVersionResponse toResponse(ScheduleVersion entity);
}