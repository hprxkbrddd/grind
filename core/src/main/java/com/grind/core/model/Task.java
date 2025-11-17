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
public class Task {
    //@Id @GeneratedValue
    private Long id;

    private String name;

    private String description;

    private String status;
}
