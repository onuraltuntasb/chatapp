package com.chatapp.chatapp.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String refreshToken;

}
