package com.base.app.auth.dto;

import com.base.domain.user.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record UserDto(
        String id,
        String email,
        String firstName,
        String lastName,
        Boolean isActive,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        String fullName,
        String initials
) {

    public static UserDto fromDomain(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail().value(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsActive(),
                null, // createdAt will be handled by mapper
                user.getFirstName() + " " + user.getLastName(),
                user.getFirstName().substring(0, 1).toUpperCase() +
                        user.getLastName().substring(0, 1).toUpperCase()
        );
    }

    public Boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    public UserDto withActive(boolean active) {
        return new UserDto(id, email, firstName, lastName, active, createdAt, fullName, initials);
    }
}