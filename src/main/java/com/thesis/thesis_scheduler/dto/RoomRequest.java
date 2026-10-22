package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomRequest {

    @NotBlank
    @Size(max = 30)
    private String roomCode;

    @Size(max = 100)
    private String roomName;

    @Size(max = 100)
    private String building;

    @NotBlank
    @Size(max = 30)
    private String roomType;

    @NotNull
    @Min(1)
    private Integer capacity;

    @NotBlank
    @Size(max = 20)
    private String status;
}