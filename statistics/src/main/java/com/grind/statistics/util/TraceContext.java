package com.grind.statistics.util;

import lombok.Getter;
import lombok.Setter;

public class TraceContext {
    @Getter
    @Setter
    private static String traceId;
}
