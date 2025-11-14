package com.progressapp.progress_tracker.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sprints")
public class Sprint {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;  // "Спринт 1", "Неделя 1", "Этап 1"

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "duration_days")
    private Integer durationDays;  // 7, 14, 30

    @Enumerated(EnumType.STRING)
    private SprintStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 🔗 СВЯЗЬ: Many Sprints to One Track
    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    // Конструкторы
    public Sprint() {}

    public Sprint(String name, LocalDate startDate, LocalDate endDate,
                  Integer durationDays, SprintStatus status, Track track) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationDays = durationDays;
        this.status = status;
        this.track = track;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public SprintStatus getStatus() {
        return status;
    }

    public void setStatus(SprintStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
}

enum SprintStatus {
    PLANNED,    // Запланирован
    ACTIVE,     // Активен
    COMPLETED   // Завершен
}