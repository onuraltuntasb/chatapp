package com.chatapp.chatapp.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckAuthRequest {
    private String email;
}
