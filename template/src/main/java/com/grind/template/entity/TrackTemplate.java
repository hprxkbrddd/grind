package com.grind.template.entity;

import com.grind.template.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "track_template")
public class TrackTemplate {
    @Id
    private String id;

    @Column(name = "author_id", nullable = false)
    private String authorId;

    @Version
    @Column
    private Integer version;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrackVisibility visibility;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrackTemplateStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "sprint_length", nullable = false)
    private Integer sprintLength;

    @Column
    @Enumerated(EnumType.STRING)
    private TrackDifficulty difficulty;

    @Column(name = "estimated_time_per_day_minutes")
    private Integer estimatedTimePerDayMinutes;

    @Column(name = "expected_result")
    private String expectedResult;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrackSkillType skillType;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private TrackCategory category;

    @ManyToMany
    @JoinTable(
            name = "track_tag",
            joinColumns = @JoinColumn(name = "track_template_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Collection<Tag> tags;

    @OneToMany(mappedBy = "trackTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TaskTemplate> taskTemplates;

    public Set<TaskTemplate> getTaskPlanningInstructions(TaskPlanningMode mode) {
        switch (mode) {
            case BACKLOG -> {
                return Set.of();
            }
            case FULL -> {
                return taskTemplates;
            }
            case PARTIAL -> {
                return taskTemplates
                        .stream()
                        .filter(TaskTemplate::getIsRequired)
                        .collect(Collectors.toSet());
            }
            default -> throw new IllegalArgumentException("Task planning mode is not recognized");
        }
    }


}
