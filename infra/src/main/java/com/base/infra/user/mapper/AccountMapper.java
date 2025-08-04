package com.base.infra.user.mapper;

import com.base.domain.user.domain.Account;
import com.base.domain.user.domain.Role;
import com.base.infra.user.entity.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

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
        domain.setUserId(entity.getUser().getId());

        return domain;
    }
}