package com.base.app.user.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileCommand(
        @NotBlank String userId,
        @NotBlank @Size(max = 50) String firstName,
        @NotBlank @Size(max = 50) String lastName
) {

    public UpdateUserProfileCommand {
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
    }

    public static UpdateUserProfileCommand of(String userId, String firstName, String lastName) {
        return new UpdateUserProfileCommand(userId, firstName, lastName);
    }
}