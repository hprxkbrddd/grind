package com.grind.core.model;

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

    private Long petId;

    private String messagePolicy;
}
