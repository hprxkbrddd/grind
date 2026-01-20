package com.grind.core.model;

import com.grind.core.dto.SprintDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sprints")
public class Sprint {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;  // "Спринт 1", "Неделя 1", "Этап 1"

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    public Sprint(){
        this.id = UUID.randomUUID().toString();
    }

    public SprintDTO mapDTO(){
        return new SprintDTO(id, name, startDate, endDate, status, createdAt, track.getId());
    }
}
