package com.base.domain.user.domain.valueobjects;

import jakarta.validation.constraints.NotBlank;

public record UserId(@NotBlank String value) {

    public UserId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId cannot be null or empty");
        }
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    public static UserId generate() {
        return new UserId(java.util.UUID.randomUUID().toString());
    }
}