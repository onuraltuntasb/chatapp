package com.chatapp.chatapp.payload.request;

import com.chatapp.chatapp.entity.Role;
import com.chatapp.chatapp.entity.User;
import com.chatapp.chatapp.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private String name;
    private String email;
    private String password;
    private List<Role> roles;
    private User.UserStatus status;
    private String loginType;
    private String adminCreationSecret;

}
