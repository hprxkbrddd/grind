package com.grind.core.model;

import com.grind.core.dto.entity.SprintDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "sprints")
public class Sprint {
    @Id
    private String id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @OneToMany(mappedBy = "sprint")
    private List<Task> tasks;

    public Sprint() {
        this.id = UUID.randomUUID().toString();
    }

    public SprintDTO mapDTO() {
        return new SprintDTO(id, startDate, endDate, track.getId());
    }
}
