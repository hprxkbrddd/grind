package com.grind.core.request.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChangeTaskDescriptionRequest {
    private String id;

    private String Description;
}
