package com.base.app.user.handler;

import com.base.app.user.command.UpdateUserProfileCommand;
import com.base.app.user.dto.UserDto;
import com.base.domain.user.domain.User;
import com.base.domain.user.domain.valueobjects.UserId;
import com.base.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateUserProfileHandler {

    private final UserRepository userRepository;

    public UserDto handle(UpdateUserProfileCommand command) {
        UserId userId = UserId.of(command.userId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userId()));

        if (!user.canUpdateProfile()) {
            throw new IllegalStateException("User profile cannot be updated");
        }

        user.updateProfile(command.firstName(), command.lastName());
        user = userRepository.save(user);

        return UserDto.fromDomain(user);
    }
}