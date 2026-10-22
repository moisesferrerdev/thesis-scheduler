
package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AcademicTermRequest {

    @NotBlank
    @Size(max = 100)
    private String termName;

    @NotBlank
    @Size(max = 20)
    private String academicYear;

    @NotBlank
    @Size(max = 30)
    private String semester;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotBlank
    @Size(max = 20)
    private String status;
}
