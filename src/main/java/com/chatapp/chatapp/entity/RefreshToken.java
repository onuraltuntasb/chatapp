package com.chatapp.chatapp.entity;

import lombok.*;

import java.time.Instant;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    private Long id;

    private String token;

    private Instant expiryDate;

    private Long user_id;

}
