package com.base.app.user.handler;


import com.base.app.user.command.ChangePasswordCommand;
import com.base.domain.user.domain.Account;
import com.base.domain.user.domain.valueobjects.Password;
import com.base.domain.user.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangePasswordHandler {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public void handle(ChangePasswordCommand command) {
        Account account = accountRepository.findByUsername(command.username())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + command.username()));

        if (!passwordEncoder.matches(command.currentPassword(), account.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        Password.raw(command.newPassword()); // Validate strength

        String encodedNewPassword = passwordEncoder.encode(command.newPassword());
        account.changePassword(Password.of(encodedNewPassword).value());

        accountRepository.save(account);
    }
}