package com.base.app.auth.handler;

import com.base.app.auth.command.LoginCommand;
import com.base.app.auth.dto.LoginResponseDto;
import com.base.infra.security.service.JwtService;
import com.base.app.user.dto.UserDto;
import com.base.domain.user.domain.Account;
import com.base.domain.user.domain.valueobjects.UserId;
import com.base.domain.user.repository.AccountRepository;
import com.base.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginHandler {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public LoginResponseDto handle(LoginCommand command) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(command.username(), command.password())
        );

        // Get account and user from domain
        Account account = accountRepository.findByUsername(command.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + command.username()));

        var user = userRepository.findById(UserId.of(account.getUserId()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Validate business rules
        if (!account.canLogin()) {
            throw new IllegalStateException("Account cannot login");
        }

        if (!user.canLogin()) {
            throw new IllegalStateException("User cannot login");
        }

        // Generate JWT token
        String accessToken = jwtService.generateToken(account);
        Long expiresIn = jwtService.getExpirationTime();

        return LoginResponseDto.of(accessToken, "Bearer", expiresIn, UserDto.fromDomain(user));
    }
}