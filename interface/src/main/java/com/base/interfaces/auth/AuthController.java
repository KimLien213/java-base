package com.base.interfaces.auth;

import com.base.app.auth.command.LoginCommand;
import com.base.app.auth.dto.LoginResponseDto;
import com.base.app.auth.handler.LoginHandler;
import com.base.app.user.command.CreateUserCommand;
import com.base.app.user.dto.UserDto;
import com.base.app.user.handler.CreateUserHandler;
import com.base.interfaces.auth.request.LoginRequest;
import com.base.interfaces.shared.response.CommonResponse;
import com.base.interfaces.user.request.CreateUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final CreateUserHandler createUserHandler;
    private final LoginHandler loginHandler;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<CommonResponse<UserDto>> register(@Valid @RequestBody CreateUserRequest request) {
        try {
            CreateUserCommand command = CreateUserCommand.of(
                    request.email(),
                    request.firstName(),
                    request.lastName(),
                    request.username(),
                    request.password()
            );

            UserDto userDto = createUserHandler.handle(command);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CommonResponse.success("User created successfully", userDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(CommonResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<CommonResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginCommand command = new LoginCommand(request.username(), request.password());
            LoginResponseDto response = loginHandler.handle(command);
            return ResponseEntity.ok(CommonResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CommonResponse.error("Login failed: " + e.getMessage()));
        }
    }
}