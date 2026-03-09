package com.grind.statistics.dto.response.ddl;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DescribeTableResponse(
        String name,
        String type,
        @JsonProperty("default_type") String defaultType,
        @JsonProperty("default_expression") String defaultExpression
) {
}
