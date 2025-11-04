package com.grind.security.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponseDTO(
        String access_token,
        Integer expires_in,
        Integer refresh_expires_in,
        String refresh_token,
        String token_type,
        String id_token,
        @JsonProperty("not-before-policy")
        Integer not_before_policy,
        String session_state,
        String scope
) {
}
