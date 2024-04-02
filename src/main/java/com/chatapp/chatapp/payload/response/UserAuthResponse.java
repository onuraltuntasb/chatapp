package com.chatapp.chatapp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthResponse {

    private String email;
    private String name;
    private Collection<? extends GrantedAuthority> authorities;
    private String jwtToken;
    private String jwtRefreshToken;
    private Boolean isAuth;


}

