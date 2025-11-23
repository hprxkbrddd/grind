package com.grind.core.model;

import com.grind.core.dto.TaskDTO;
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
    private String id;

    private String sprintId;

    private String name;

    private String description;

    private String status;

    public TaskDTO mapDTO(){
        return new TaskDTO(id, sprintId, name, description, status);
    }
}
