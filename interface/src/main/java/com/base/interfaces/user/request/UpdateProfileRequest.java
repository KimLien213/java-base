package com.base.interfaces.user.request;

import com.base.app.user.command.UpdateUserProfileCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName
) {

    public UpdateProfileRequest {
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
    }

    public UpdateUserProfileCommand toCommand(String userId) {
        return UpdateUserProfileCommand.of(userId, firstName, lastName);
    }
}
