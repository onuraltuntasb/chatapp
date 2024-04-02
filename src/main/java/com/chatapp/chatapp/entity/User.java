package com.chatapp.chatapp.entity;

import com.chatapp.chatapp.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class User implements UserDetails {

    public User(Long id, String name, String email, UserStatus status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
    }

    public enum UserStatus {
        ACTIVE, CLOSED, CANCELED, BLACKLISTED, NONE
    }

    private Long id;

    @NotBlank
    @Pattern(regexp = "^(?=[a-zA-Z0-9._]{6,20}$)(?!.*[_.]{2})[^_.].*[^_.]$")
    private String name;

    @Email
    private String email;

    @NotBlank
    @ValidPassword
    private String password;

    private UserStatus status;

    private String loginType;

    private List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();
        for (Role r : getRoles()) {
            list.add(new SimpleGrantedAuthority(r.getName()));
        }
        return list;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
