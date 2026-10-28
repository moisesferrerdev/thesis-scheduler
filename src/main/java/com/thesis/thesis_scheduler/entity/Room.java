
package com.thesis.thesis_scheduler.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "room",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_room_room_code",
                        columnNames = "room_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "room_code", nullable = false, length = 30, unique = true)
    private String roomCode;

    @Column(name = "room_name", length = 100)
    private String roomName;

    @Column(name = "building", length = 100)
    private String building;

    @Column(name = "room_type", nullable = false, length = 30)
    private String roomType;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

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
