package com.base.interfaces.auth;

import com.base.app.auth.command.LoginCommand;
import com.base.app.auth.dto.LoginResponseDto;
import com.base.app.auth.handler.LoginHandler;
import com.base.app.user.command.CreateUserCommand;
import com.base.app.user.dto.UserDto;
import com.base.app.user.handler.CreateUserHandler;
import com.base.interfaces.auth.request.LoginRequest;
import com.base.interfaces.shared.response.ApiResponse;
import com.base.interfaces.user.request.CreateUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CreateUserHandler createUserHandler;
    private final LoginHandler loginHandler;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody CreateUserRequest request) {
        CreateUserCommand command = CreateUserCommand.of(
                request.email(),
                request.firstName(),
                request.lastName(),
                request.username(),
                request.password()
        );

        UserDto userDto = createUserHandler.handle(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.username(), request.password());

        LoginResponseDto response = loginHandler.handle(command);

        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}