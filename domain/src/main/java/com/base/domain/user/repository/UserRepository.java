package com.base.domain.user.repository;

import com.base.domain.user.domain.User;
import com.base.domain.user.domain.valueobjects.Email;
import com.base.domain.user.domain.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId userId);
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
    List<User> findByActiveStatus(boolean isActive);
    void delete(User user);
    long countByActiveStatus(boolean isActive);
}
