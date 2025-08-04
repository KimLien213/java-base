package com.base.infra.user.repository.impl;

import com.base.domain.user.domain.Account;
import com.base.domain.user.domain.Role;
import com.base.domain.user.domain.valueobjects.UserId;
import com.base.domain.user.repository.AccountRepository;
import com.base.infra.user.entity.AccountEntity;
import com.base.infra.user.mapper.AccountMapper;
import com.base.infra.user.repository.JpaAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final JpaAccountRepository jpaRepository;
    private final AccountMapper mapper;

    @Override
    public Account save(Account account) {
        AccountEntity entity = mapper.toEntity(account);
        AccountEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Optional<Account> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.value()).map(mapper::toDomain);
    }

    @Override
    public List<Account> findByRole(Role role) {
        return jpaRepository.findByRole(Role.valueOf(role.name())).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Account> findByEnabledStatus(boolean isEnabled) {
        return jpaRepository.findByIsEnabled(isEnabled).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public void delete(Account account) {
        AccountEntity entity = mapper.toEntity(account);
        jpaRepository.delete(entity);
    }

    @Override
    public long countByRole(Role role) {
        return jpaRepository.countByRole(Role.valueOf(role.name()));
    }

    @Override
    public List<Account> findLockedAccounts() {
        return jpaRepository.findLockedAccounts().stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
