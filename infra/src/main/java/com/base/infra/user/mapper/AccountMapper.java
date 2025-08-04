package com.base.infra.user.mapper;

import com.base.domain.user.domain.Account;
import com.base.domain.user.domain.Role;
import com.base.infra.user.entity.AccountEntity;
import com.base.infra.user.entity.UserEntity;
import com.base.infra.user.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountMapper {

    private final JpaUserRepository userRepository;

    public AccountEntity toEntity(Account domain) {
        if (domain == null) return null;

        AccountEntity entity = new AccountEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setRole(Role.valueOf(domain.getRole().name()));
        entity.setIsEnabled(domain.getIsEnabled());
        entity.setIsAccountNonExpired(domain.getIsAccountNonExpired());
        entity.setIsAccountNonLocked(domain.getIsAccountNonLocked());
        entity.setIsCredentialsNonExpired(domain.getIsCredentialsNonExpired());

        // Set required User relationship
        if (domain.getUserId() != null) {
            UserEntity userEntity = userRepository.findById(domain.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + domain.getUserId()));
            entity.setUser(userEntity);
            log.debug("Set user relationship for account: {} -> user: {}", domain.getId(), domain.getUserId());
        } else {
            throw new IllegalArgumentException("Account must have a userId");
        }

        return entity;
    }

    public Account toDomain(AccountEntity entity) {
        if (entity == null) return null;

        Account domain = new Account();
        domain.setId(entity.getId());
        domain.setUsername(entity.getUsername());
        domain.setPassword(entity.getPassword());
        domain.setRole(Role.valueOf(entity.getRole().name()));
        domain.setIsEnabled(entity.getIsEnabled());
        domain.setIsAccountNonExpired(entity.getIsAccountNonExpired());
        domain.setIsAccountNonLocked(entity.getIsAccountNonLocked());
        domain.setIsCredentialsNonExpired(entity.getIsCredentialsNonExpired());

        // Get userId from required User relationship
        if (entity.getUser() != null) {
            domain.setUserId(entity.getUser().getId());
        } else {
            throw new IllegalStateException("AccountEntity must have a User relationship");
        }

        return domain;
    }
}