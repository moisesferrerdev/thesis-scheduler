package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.TimeslotRequest;
import com.thesis.thesis_scheduler.dto.TimeslotResponse;
import com.thesis.thesis_scheduler.entity.Timeslot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimeslotMapper {

    @Mapping(target = "timeslotId", ignore = true)
    @Mapping(target = "term", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Timeslot toEntity(TimeslotRequest request);

    @Mapping(target = "termId", source = "term.termId")
    @Mapping(target = "termName", source = "term.termName")
    TimeslotResponse toResponse(Timeslot entity);
}