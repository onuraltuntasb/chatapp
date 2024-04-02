package com.chatapp.chatapp.payload.response;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class ResetPasswordTokenResponse {

    private Long id;
    private String token;
    private Long  userId;
    private Date expiryDate;

}
