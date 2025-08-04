package com.base.domain.user.domain;

import com.base.domain.shared.BaseDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@NoArgsConstructor
public class Account extends BaseDomain {

    private String username;
    private String password;
    private Role role = Role.USER;
    private Boolean isEnabled = true;
    private Boolean isAccountNonExpired = true;
    private Boolean isAccountNonLocked = true;
    private Boolean isCredentialsNonExpired = true;
    private String userId;

    // Factory method
    public static Account create(String username, String encodedPassword, String userId) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(encodedPassword);
        account.setRole(Role.USER);
        account.setUserId(userId);
        return account;
    }

    public Collection<String> getAuthorities() {
        return Collections.singletonList("ROLE_" + role.name());
    }

    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void lock(String reason) {
        this.isAccountNonLocked = false;
    }

    public void unlock() {
        this.isAccountNonLocked = true;
    }

    public void disable(String reason) {
        this.isEnabled = false;
    }

    public void enable() {
        this.isEnabled = true;
    }

    public void changePassword(String newEncodedPassword) {
        this.password = newEncodedPassword;
    }

    public boolean canLogin() {
        return isEnabled && isAccountNonExpired &&
                isAccountNonLocked && isCredentialsNonExpired;
    }

    public boolean hasRole(Role role) {
        return this.role == role;
    }
}