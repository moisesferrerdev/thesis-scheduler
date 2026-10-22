
package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProgramRequest {

    @NotBlank
    @Size(max = 20)
    private String programCode;

    @NotBlank
    @Size(max = 150)
    private String programName;

    private String description;

    @NotBlank
    @Size(max = 20)
    private String status;
}
