package com.grind.core.model;

import com.grind.core.dto.entity.TrackDTO;
import com.grind.core.enums.TrackStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table(name = "tracks")
public class Track {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "pet_id")
    private String petId;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "sprint_length")
    private Integer sprintLength;

    @Column(name = "message_policy")
    private String messagePolicy;

    @Column
    @Enumerated(EnumType.STRING)
    private TrackStatus status;

    @Column(name = "user_id")
    private String userId;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sprint> sprints = new ArrayList<>();

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    public Track() {
        this.id = UUID.randomUUID().toString();
    }

    public TrackDTO mapDTO() {
        return new TrackDTO(
                id,
                name,
                description,
                petId,
                durationDays,
                startDate,
                targetDate,
                createdAt,
                messagePolicy,
                status,
                userId
        );
    }
}
