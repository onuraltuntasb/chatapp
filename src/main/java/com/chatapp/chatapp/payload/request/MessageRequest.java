package com.chatapp.chatapp.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageRequest {
    private String content;
    private String senderEmail;
    private String receiverEmail;
}