package com.base.domain.user.repository;

import com.base.domain.user.domain.Account;
import com.base.domain.user.domain.Role;
import com.base.domain.user.domain.valueobjects.Email;
import com.base.domain.user.domain.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findByUsername(String username);
    Optional<Account> findByUserId(UserId userId);
    List<Account> findByRole(Role role);
    List<Account> findByEnabledStatus(boolean isEnabled);
    boolean existsByUsername(String username);
    void delete(Account account);

    long countByRole(Role role);
    List<Account> findLockedAccounts();
}
