package com.grind.core.model;

import com.grind.core.dto.SprintDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sprints")
public class Sprint {

    @Id
    @GeneratedValue
    private String id;

    @Column(nullable = false)
    private String name;  // "Спринт 1", "Неделя 1", "Этап 1"

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private SprintStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;
    public SprintDTO mapDTO(){
        return new SprintDTO(id, trackId, duration);
    }
}
