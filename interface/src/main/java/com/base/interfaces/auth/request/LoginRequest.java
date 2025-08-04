package com.base.interfaces.auth.request;

import com.base.app.auth.command.LoginCommand;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {

    public LoginRequest {
        if (username != null) {
            username = username.trim();
        }
    }

    public LoginCommand toCommand() {
        return LoginCommand.of(username, password);
    }
}
