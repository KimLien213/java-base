package com.base.app.auth.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordCommand(
        @NotBlank String username,
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8) String newPassword
) {

    public static ChangePasswordCommand of(String username, String currentPassword, String newPassword) {
        return new ChangePasswordCommand(username, currentPassword, newPassword);
    }
}