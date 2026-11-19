package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(
        name = "scheduling_config",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_scheduling_config_term",
                        columnNames = "term_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SchedulingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Long configId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "term_id",
            nullable = false
    )
    private AcademicTerm term;

    @Column(name = "operating_days", nullable = false, length = 20)
    private String operatingDays;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Column(name = "timeslot_interval_minutes", nullable = false)
    private Integer timeslotIntervalMinutes;
}