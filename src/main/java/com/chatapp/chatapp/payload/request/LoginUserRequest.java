package com.chatapp.chatapp.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginUserRequest {
    private String email;
    private String password;
}
