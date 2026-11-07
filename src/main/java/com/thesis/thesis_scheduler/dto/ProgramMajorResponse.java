
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
public class ProgramMajorResponse {

    private Long programMajorId;

    private Long programId;
    private String programCode;
    private String programName;

    private String majorCode;
    private String majorName;
    private String description;
    private String status;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
