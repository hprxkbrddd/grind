package com.progressapp.progress_tracker.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tracks")
public class Track {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    private TrackStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // СВЯЗЬ: Many Tracks to One User
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Конструкторы
    public Track() {}

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public TrackStatus getStatus() {
        return status;
    }

    public void setStatus(TrackStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Track(String name, String description, Integer durationDays,
                 LocalDate targetDate, TrackStatus status, User owner) {
        this.name = name;
        this.description = description;
        this.durationDays = durationDays;
        this.targetDate = targetDate;
        this.status = status;
        this.owner = owner;
    }



}

// Enum для статусов трека
enum TrackStatus {
    ACTIVE, COMPLETED, ARCHIVED
}