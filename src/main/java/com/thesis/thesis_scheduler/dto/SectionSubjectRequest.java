
package com.thesis.thesis_scheduler.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SectionSubjectRequest {

    @NotNull
    private Long sectionId;

    @NotNull
    private Long offeringId;
}
