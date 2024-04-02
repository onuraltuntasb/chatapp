package com.chatapp.chatapp.controller;

import com.chatapp.chatapp.payload.request.AddFriendRequest;
import com.chatapp.chatapp.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/save")
    public ResponseEntity<?> addFriend(@Valid @RequestBody AddFriendRequest addFriendRequest){
        Boolean response = friendService.addFriend(addFriendRequest);
        return ResponseEntity.ok().body(response.toString());
    }
}
