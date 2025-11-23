package com.grind.core.request.Track;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateTrackRequest {
    private String name;

    private String description;

    private String petId;

    private String messagePolicy;
}
