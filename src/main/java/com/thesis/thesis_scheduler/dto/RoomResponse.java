package com.thesis.thesis_scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long roomId;

    private String roomCode;
    private String roomName;
    private String building;
    private String roomType;
    private Integer capacity;
    private String status;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}