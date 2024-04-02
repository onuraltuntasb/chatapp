package com.chatapp.chatapp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersRolesResponse {
    private Long user_id;
    private Long role_id;
}
