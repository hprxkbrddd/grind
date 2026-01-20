package com.grind.core.model;

import com.grind.core.dto.TrackDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tracks")
public class Track {
    @Id
    @GeneratedValue
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "pet_id")
    private String petId;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "message_policy")
    private String messagePolicy;

    @Column(name = "status")
    private String status;

    public TrackDTO mapDTO(){
        return new TrackDTO(id, name, description, petId, durationDays, targetDate, createdAt, messagePolicy, status);
    }
}
