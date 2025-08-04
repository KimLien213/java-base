package com.base.infra.user.repository;

import com.base.domain.user.domain.Role;
import com.base.infra.user.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, String> {

    // Eager load User relationship for username lookup (needed for authentication)
    @Query("SELECT a FROM AccountEntity a JOIN FETCH a.user WHERE a.username = :username")
    Optional<AccountEntity> findByUsername(@Param("username") String username);

    // Standard methods
    @Query("SELECT a FROM AccountEntity a WHERE a.user.id = :userId")
    Optional<AccountEntity> findByUserId(@Param("userId") String userId);

    boolean existsByUsername(String username);
    List<AccountEntity> findByRole(Role role);
    List<AccountEntity> findByIsEnabled(boolean isEnabled);
    long countByRole(Role role);

    @Query("SELECT a FROM AccountEntity a WHERE a.isAccountNonLocked = false")
    List<AccountEntity> findLockedAccounts();
}