package com.grind.core.model;

import com.grind.core.dto.entity.TaskDTO;
import com.grind.core.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @Column(name = "planned_date")
    private LocalDate plannedDate;

    @Column(name = "actual_date")
    private LocalDate actualDate;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Task() {
        this.id = UUID.randomUUID().toString();
    }

    public TaskDTO mapDTO() {
        return new TaskDTO(id, title, sprint == null ? "" : sprint.getId(), plannedDate, actualDate, description, status, createdAt);
    }
}
