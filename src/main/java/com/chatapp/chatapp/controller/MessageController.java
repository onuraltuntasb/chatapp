package com.chatapp.chatapp.controller;


import com.chatapp.chatapp.payload.request.MessageRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("api/message")
public class MessageController {


    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageRequest messageRequest){
        //send messages function in service
        //function must be use messages.save
        //

        return ResponseEntity.ok().body("true");
    }

}
