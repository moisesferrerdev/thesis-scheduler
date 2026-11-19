package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomClassificationRequest {

    @NotBlank
    @Size(max = 30)
    private String roomType;

    @NotBlank
    @Size(max = 100)
    private String classificationName;

    @Size(max = 500)
    private String description;

    @NotBlank
    @Size(max = 20)
    private String status;
}