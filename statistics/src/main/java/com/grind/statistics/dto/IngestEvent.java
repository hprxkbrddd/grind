package com.grind.statistics.dto;

import java.util.List;

public record IngestEvent(
        List<Object> values,
        Boolean successful
) {
}
