package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "program",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_program_program_code",
                        columnNames = "program_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private Long programId;

    @Column(name = "program_code", nullable = false, length = 20, unique = true)
    private String programCode;

    @Column(name = "program_name", nullable = false, length = 150)
    private String programName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

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