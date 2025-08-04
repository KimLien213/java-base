package com.base.app.auth.command;


import jakarta.validation.constraints.NotBlank;

public record LoginCommand(@NotBlank String username, @NotBlank String password) {

    public LoginCommand {
        if (username != null) {
            username = username.trim();
        }
    }

    public static LoginCommand of(String username, String password) {
        return new LoginCommand(username, password);
    }
}
