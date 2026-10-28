
package com.thesis.thesis_scheduler.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "performance_evaluation")
@Getter
@Setter
@NoArgsConstructor
public class PerformanceEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Long evaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_schedule_id")
    private GeneratedSchedule generatedSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_version_id")
    private ScheduleVersion scheduleVersion;

    @Column(name = "hard_constraint_score", precision = 12, scale = 4)
    private BigDecimal hardConstraintScore;

    @Column(name = "soft_constraint_score", precision = 12, scale = 4)
    private BigDecimal softConstraintScore;

    @Column(name = "fitness_score", precision = 12, scale = 4)
    private BigDecimal fitnessScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "evaluation_details", columnDefinition = "jsonb")
    private JsonNode evaluationDetails;

    @Column(name = "evaluated_at", nullable = false)
    private OffsetDateTime evaluatedAt;

    @PrePersist
    protected void onCreate() {
        if (evaluatedAt == null) {
            evaluatedAt = OffsetDateTime.now();
        }
    }
}
