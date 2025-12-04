package com.grind.core.request.Sprint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChangeSprintNameRequest {
    private String id;

    private String name;
}