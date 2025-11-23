package com.grind.core.model;

import com.grind.core.dto.TrackDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Node
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Track {
    //@Id @GeneratedValue
    private Long id;

    private String name;

    private String description;

    private String petId;

    private String messagePolicy;

    public TrackDTO mapDTO(){
        return new TrackDTO(id, name, description, petId, messagePolicy);
    }
}
