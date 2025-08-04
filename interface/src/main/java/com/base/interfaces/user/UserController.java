package com.base.interfaces.user;

import com.base.app.user.handler.UpdateUserProfileHandler;
import com.base.app.user.dto.UserDto;
import com.base.app.user.handler.ChangePasswordHandler;
import com.base.domain.user.domain.valueobjects.UserId;
import com.base.domain.user.repository.UserRepository;
import com.base.infra.user.entity.AccountEntity;
import com.base.interfaces.shared.response.ApiResponse;
import com.base.interfaces.user.request.ChangePasswordRequest;
import com.base.interfaces.user.request.UpdateProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Management", description = "User management endpoints")
public class UserController {

    private final UserRepository userRepository;
    private final UpdateUserProfileHandler updateUserProfileHandler;
    private final ChangePasswordHandler changePasswordHandler;

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user information")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(Authentication authentication) {
        AccountEntity accountEntity = (AccountEntity) authentication.getPrincipal();

        var user = userRepository.findById(UserId.of(accountEntity.getUser().getId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDto userDto = UserDto.fromDomain(user);

        return ResponseEntity.ok(ApiResponse.success(userDto));
    }

    @PutMapping("/me/profile")
    @Operation(summary = "Update profile", description = "Update current user profile information")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        AccountEntity accountEntity = (AccountEntity) authentication.getPrincipal();
        String userId = accountEntity.getUser().getId();

        UserDto userDto = updateUserProfileHandler.handle(request.toCommand(userId));

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userDto));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change password", description = "Change current user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        AccountEntity accountEntity = (AccountEntity) authentication.getPrincipal();

        changePasswordHandler.handle(request.toCommand(accountEntity.getUsername()));

        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}
