package com.chatapp.chatapp.payload.request;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmNewPassword;
}
