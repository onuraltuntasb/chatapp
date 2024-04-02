package com.chatapp.chatapp.payload.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddFriendRequest {
    @Email
    private String UserEmail;
    @Email
    private String FriendEmail;
}
