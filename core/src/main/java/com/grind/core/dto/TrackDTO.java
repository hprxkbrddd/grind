package com.grind.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class TrackDTO {
    private String name;

    private String description;

    private Long petId;

    private String messagePolicy;
}
