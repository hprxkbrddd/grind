package com.grind.security.dto;

public record TokenIntrospectionResponse(
         boolean active,
         String sub,
         String username,
         String email,
         Long exp,
         Long iat,
         String scope,
         String token_type,
         String client_id

) {
}
