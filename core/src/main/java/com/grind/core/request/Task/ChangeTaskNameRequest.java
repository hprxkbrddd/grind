package com.grind.core.request.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChangeTaskNameRequest {
    private String id;

    private String name;
}
