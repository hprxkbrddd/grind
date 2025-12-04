package com.grind.core.request.Track;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChangeTrackStatusRequest {
    private String id;

    private String status;
}
