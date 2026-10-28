
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
@Table(name = "scheduling_constraint")
@Getter
@Setter
@NoArgsConstructor
public class SchedulingConstraint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "constraint_id")
    private Long constraintId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false)
    private AcademicTerm term;

    @Column(name = "constraint_code", nullable = false, length = 30)
    private String constraintCode;

    @Column(name = "constraint_name", nullable = false, length = 150)
    private String constraintName;

    @Column(name = "constraint_type", nullable = false, length = 20)
    private String constraintType;

    @Column(name = "constraint_category", nullable = false, length = 20)
    private String constraintCategory;

    @Column(name = "weight", precision = 8, scale = 3)
    private BigDecimal weight;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuration", columnDefinition = "jsonb")
    private JsonNode configuration;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
