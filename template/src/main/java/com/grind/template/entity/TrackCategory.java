package com.grind.template.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "track_category")
public class TrackCategory {
    @Id
    private String id;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String slug;

    @Column(name = "sort_order", nullable = false)
    private Integer order;
}
