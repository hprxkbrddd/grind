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
public class Sprint {

    private String id;

    private String trackId;

    private short duration;

    public SprintDTO mapDTO(){
        return new SprintDTO(id, trackId, duration);
    }
}
