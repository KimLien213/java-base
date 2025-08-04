package com.base.infra.user.mapper;

import com.base.domain.user.domain.User;
import com.base.infra.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail().value());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setIsActive(domain.getIsActive());

        return entity;
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        User domain = new User();
        domain.setId(entity.getId());
        domain.setEmail(entity.getEmail());
        domain.setFirstName(entity.getFirstName());
        domain.setLastName(entity.getLastName());
        domain.setIsActive(entity.getIsActive());

        return domain;
    }
}
