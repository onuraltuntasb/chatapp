package com.chatapp.chatapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    private Long id;

    private Long user_id;

    private Long group_id;

    private Long chat_id;

    private String content;

    private Instant sentAt;

    private Instant deliveredAt;

    private Instant seenAt;

}