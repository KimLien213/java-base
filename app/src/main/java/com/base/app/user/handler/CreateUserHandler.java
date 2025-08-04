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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserHandler {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto handle(CreateUserCommand command) {
        // Create value objects from command
        Email email = Email.of(command.email());

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email already exists: " + email.value());
        }

        if (accountRepository.existsByUsername(command.username())) {
            throw new IllegalArgumentException("Username already exists: " + command.username());
        }

        // Validate password strength before encoding
        Password.raw(command.password());

        // Create user (pure domain object)
        User user = User.create(email, command.firstName(), command.lastName());
        user = userRepository.save(user);

        // Create account (pure domain object)
        String encodedPassword = passwordEncoder.encode(command.password());
        Account account = Account.create(command.username(), encodedPassword, user.getId());
        accountRepository.save(account);

        return UserDto.fromDomain(user);
    }
}