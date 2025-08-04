package com.base.app.user.handler;

import com.base.app.user.command.CreateUserCommand;
import com.base.app.user.dto.UserDto;
import com.base.domain.user.domain.Account;
import com.base.domain.user.domain.User;
import com.base.domain.user.domain.valueobjects.Email;
import com.base.domain.user.domain.valueobjects.Password;
import com.base.domain.user.repository.AccountRepository;
import com.base.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateUserHandler {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto handle(CreateUserCommand command) {
        log.info("Starting user creation process for email: {}", command.email());

        try {
            // Step 1: Validate input data
            validateInput(command);

            // Step 2: Create and save User entity
            User user = createUser(command);

            // Step 3: Create and save Account entity with reference to User
            Account account = createAccount(command, user.getId());

            log.info("Successfully created user: {} with account: {}", user.getId(), account.getId());
            return UserDto.fromDomain(user);

        } catch (IllegalArgumentException e) {
            log.warn("User creation validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during user creation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    private void validateInput(CreateUserCommand command) {
        log.debug("Validating input for user creation");

        // Create and validate email
        Email email = Email.of(command.email());

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email already exists: " + email.value());
        }

        // Check if username already exists
        if (accountRepository.existsByUsername(command.username())) {
            throw new IllegalArgumentException("Username already exists: " + command.username());
        }

        // Validate password strength
        try {
            Password.raw(command.password());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Password validation failed: " + e.getMessage());
        }

        log.debug("Input validation passed");
    }

    private User createUser(CreateUserCommand command) {
        log.debug("Creating user entity");

        Email email = Email.of(command.email());
        User user = User.create(email, command.firstName(), command.lastName());
        user = userRepository.save(user);

        log.info("User entity created with ID: {}", user.getId());
        return user;
    }

    private Account createAccount(CreateUserCommand command, String userId) {
        log.debug("Creating account entity for user: {}", userId);

        String encodedPassword = passwordEncoder.encode(command.password());
        Account account = Account.create(command.username(), encodedPassword, userId);
        account = accountRepository.save(account);

        log.info("Account entity created with ID: {} for user: {}", account.getId(), userId);
        return account;
    }
}