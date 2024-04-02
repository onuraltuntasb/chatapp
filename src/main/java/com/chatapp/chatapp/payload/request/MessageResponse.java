package com.chatapp.chatapp.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageResponse {
    private Boolean status ;
    private String senderEmail;
    private String receiverEmail;
}
