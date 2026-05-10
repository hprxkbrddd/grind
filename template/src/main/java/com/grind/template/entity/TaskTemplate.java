package com.grind.template.entity;

import com.grind.template.dto.entity.TaskTemplateDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "task_template")
public class TaskTemplate {
    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "track_template", nullable = false)
    private TrackTemplate trackTemplate;

    @Column(name = "planned_day_offset", nullable = false)
    private Integer plannedDayOffset;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    @Column(name = "task_group")
    private String taskGroup;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TaskTemplateDTO mapDTO(){
        return new TaskTemplateDTO(
                this.id,
                this.title,
                this.description,
                this.plannedDayOffset
        );
    }
}
