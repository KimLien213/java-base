package com.base.app.user.command;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserCommand(
        @NotBlank @Email String email,
        @NotBlank @Size(max = 50) String firstName,
        @NotBlank @Size(max = 50) String lastName,
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 8) String password
) {

    public CreateUserCommand {
        if (email != null) {
            email = email.toLowerCase().trim();
        }
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
        if (username != null) {
            username = username.trim();
        }
    }

    public static CreateUserCommand of(String email, String firstName, String lastName,
                                       String username, String password) {
        return new CreateUserCommand(email, firstName, lastName, username, password);
    }
}
