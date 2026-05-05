package com.grind.template.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String slug;

    @ManyToMany(mappedBy = "tags")
    private Set<TrackTemplate> tracks;
}
