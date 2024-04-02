package com.chatapp.chatapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Friend {

    private Long id;
    private Long user_id;
    private Long friend_id;
}
