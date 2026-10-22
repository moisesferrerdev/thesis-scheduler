
package com.thesis.thesis_scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponse {

    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private String description;
    private BigDecimal lectureHours;
    private BigDecimal laboratoryHours;
    private BigDecimal units;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
