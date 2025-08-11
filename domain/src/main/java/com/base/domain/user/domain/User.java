package com.base.domain.user.domain;

import com.base.domain.shared.BaseDomain;
import com.base.domain.user.domain.valueobjects.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User extends BaseDomain {

    private Email email;
    private String firstName;
    private String lastName;
    private Boolean isActive = true;

    public static User create(Email email, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setIsActive(true);
        return user;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public boolean canLogin() {
        return isActive;
    }

    public void updateProfile(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

}