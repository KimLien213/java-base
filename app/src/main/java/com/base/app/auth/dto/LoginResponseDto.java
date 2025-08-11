package com.base.app.auth.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record LoginResponseDto(
        String accessToken,
        String tokenType,
        Long expiresIn,
        UserDto user,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime loginTime
) {

    public static LoginResponseDto of(String accessToken, String tokenType, Long expiresIn, UserDto user) {
        return new LoginResponseDto(accessToken, tokenType, expiresIn, user, LocalDateTime.now());
    }

    // Convenience methods
    public boolean isValid() {
        return accessToken != null && !accessToken.isEmpty() && user != null;
    }

    public LocalDateTime expiresAt() {
        return loginTime.plusSeconds(expiresIn / 1000);
    }
}
