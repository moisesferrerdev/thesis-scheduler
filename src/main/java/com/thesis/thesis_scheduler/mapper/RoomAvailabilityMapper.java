package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.RoomAvailabilityRequest;
import com.thesis.thesis_scheduler.dto.RoomAvailabilityResponse;
import com.thesis.thesis_scheduler.entity.RoomAvailability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomAvailabilityMapper {

    @Mapping(target = "roomAvailabilityId", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "term", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RoomAvailability toEntity(RoomAvailabilityRequest request);

    @Mapping(target = "roomId", source = "room.roomId")
    @Mapping(target = "roomCode", source = "room.roomCode")
    @Mapping(target = "roomName", source = "room.roomName")
    @Mapping(target = "termId", source = "term.termId")
    @Mapping(target = "termName", source = "term.termName")
    RoomAvailabilityResponse toResponse(RoomAvailability entity);

    @Mapping(target = "roomAvailabilityId", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "term", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(
            RoomAvailabilityRequest request,
            @MappingTarget RoomAvailability entity
    );
}