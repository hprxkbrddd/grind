package com.grind.core.request.Track;

import java.time.LocalDate;

public record ChangeTrackRequest(        String name,

        String description,

        String petId,

        Integer durationDays,

        LocalDate targetDate,

        String messagePolicy,
        
        String status) {
    
}
