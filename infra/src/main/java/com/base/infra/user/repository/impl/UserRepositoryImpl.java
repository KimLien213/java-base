package com.base.infra.user.repository.impl;

import com.base.domain.user.domain.User;
import com.base.domain.user.domain.valueobjects.Email;
import com.base.domain.user.domain.valueobjects.UserId;
import com.base.domain.user.repository.UserRepository;
import com.base.infra.user.entity.UserEntity;
import com.base.infra.user.mapper.UserMapper;
import com.base.infra.user.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return jpaRepository.findById(userId.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public List<User> findByActiveStatus(boolean isActive) {
        return jpaRepository.findByIsActive(isActive)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(User user) {
        UserEntity entity = mapper.toEntity(user);
        jpaRepository.delete(entity);
    }

    @Override
    public long countByActiveStatus(boolean isActive) {
        return jpaRepository.countByIsActive(isActive);
    }
}

