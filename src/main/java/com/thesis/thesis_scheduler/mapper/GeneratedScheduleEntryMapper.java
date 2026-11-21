package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.GeneratedScheduleEntryResponse;
import com.thesis.thesis_scheduler.entity.GeneratedScheduleEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GeneratedScheduleEntryMapper {

    @Mapping(
            target = "generatedScheduleId",
            source = "generatedSchedule.generatedScheduleId"
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
    GeneratedScheduleEntryResponse toResponse(
            GeneratedScheduleEntry entity
    );
}