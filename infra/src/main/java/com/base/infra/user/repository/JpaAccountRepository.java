package com.base.infra.user.repository;

import com.base.domain.user.domain.Role;
import com.base.infra.user.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, String> {
    Optional<AccountEntity> findByUsername(String username);
    Optional<AccountEntity> findByUserId(String userId);
    boolean existsByUsername(String username);
    List<AccountEntity> findByRole(Role role);
    List<AccountEntity> findByIsEnabled(boolean isEnabled);
    long countByRole(Role role);

    @Query("SELECT a FROM AccountEntity a WHERE a.isAccountNonLocked = false")
    List<AccountEntity> findLockedAccounts();
}
