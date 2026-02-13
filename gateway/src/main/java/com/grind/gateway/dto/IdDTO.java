package com.grind.gateway.dto;

public record IdDTO(
        String id
) {
    public static IdDTO of(String val){
        return new IdDTO(val);
    }
}
