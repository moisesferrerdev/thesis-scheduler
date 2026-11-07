
package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProgramMajorRequest {

    @NotNull
    private Long programId;

    @NotBlank
    @Size(max = 30)
    private String majorCode;

    @NotBlank
    @Size(max = 150)
    private String majorName;

    private String description;

    @NotBlank
    @Size(max = 20)
    private String status;
}
