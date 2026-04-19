package com.grind.gateway.dto.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Diagram data series")
public record DiagramDTO(
        @Schema(description = "Ordered diagram points")
        List<DiagramUnitDTO> diagram
) {
}
