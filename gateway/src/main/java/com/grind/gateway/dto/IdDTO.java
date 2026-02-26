package com.grind.gateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Identifier wrapper")
public record IdDTO(
        @Schema(description = "Identifier value")
        String id
) {
    public static IdDTO of(String val){
        return new IdDTO(val);
    }
}
