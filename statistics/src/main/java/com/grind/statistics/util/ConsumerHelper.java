package com.grind.statistics.util;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class ConsumerHelper {
    public static void authenticate(String userId, String roles) {
        List<SimpleGrantedAuthority> authorities = Arrays.stream(
                        roles != null ? roles.split(",") : new String[0]
                )
                .map(String::trim)
                .filter(r -> !r.isBlank())
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .toList();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static String headerAsString(ConsumerRecord<?, ?> rec, String name) {
        var h = rec.headers().lastHeader(name);
        return (h == null || h.value() == null) ? null : new String(h.value(), StandardCharsets.UTF_8);
    }
}
