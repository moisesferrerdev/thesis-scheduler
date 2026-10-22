package com.thesis.thesis_scheduler.dto;

import com.fasterxml.jackson.databind.JsonNode;
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
public class SchedulingConstraintResponse {

    private Long constraintId;

    private Long termId;
    private String termName;

    private String constraintCode;
    private String constraintName;
    private String constraintType;
    private String constraintCategory;

    private BigDecimal weight;
    private JsonNode configuration;
    private Boolean active;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}