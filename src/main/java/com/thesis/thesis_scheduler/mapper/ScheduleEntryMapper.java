package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.ScheduleEntryRequest;
import com.thesis.thesis_scheduler.dto.ScheduleEntryResponse;
import com.thesis.thesis_scheduler.entity.ScheduleEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleEntryMapper {

    @Mapping(target = "scheduleEntryId", ignore = true)
    @Mapping(target = "scheduleVersion", ignore = true)
    @Mapping(target = "assignment", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "timeslot", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ScheduleEntry toEntity(ScheduleEntryRequest request);

    @Mapping(
            target = "scheduleVersionId",
            source = "scheduleVersion.scheduleVersionId"
    )
    @Mapping(
            target = "versionNumber",
            source = "scheduleVersion.versionNumber"
    )
    @Mapping(
            target = "assignmentId",
            source = "assignment.assignmentId"
    )
    @Mapping(
            target = "instructorName",
            expression = "java(entity.getAssignment().getInstructor().getFirstName() + \" \" + entity.getAssignment().getInstructor().getLastName())"
    )
    @Mapping(
            target = "sectionCode",
            source = "assignment.sectionSubject.section.sectionCode"
    )
    @Mapping(
            target = "offeringCode",
            source = "assignment.sectionSubject.offering.offeringCode"
    )
    @Mapping(target = "roomId", source = "room.roomId")
    @Mapping(target = "roomCode", source = "room.roomCode")
    @Mapping(target = "timeslotId", source = "timeslot.timeslotId")
    @Mapping(target = "dayOfWeek", source = "timeslot.dayOfWeek")
    @Mapping(target = "startTime", source = "timeslot.startTime")
    @Mapping(target = "endTime", source = "timeslot.endTime")
    @Mapping(target = "slotLabel", source = "timeslot.slotLabel")
    ScheduleEntryResponse toResponse(ScheduleEntry entity);
}