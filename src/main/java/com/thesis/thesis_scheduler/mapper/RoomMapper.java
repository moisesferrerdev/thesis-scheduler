package com.thesis.thesis_scheduler.mapper;

import com.thesis.thesis_scheduler.dto.RoomRequest;
import com.thesis.thesis_scheduler.dto.RoomResponse;
import com.thesis.thesis_scheduler.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "roomId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Room toEntity(RoomRequest request);

    RoomResponse toResponse(Room entity);

    @Mapping(target = "roomId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            RoomRequest request,
            @MappingTarget Room entity
    );
}