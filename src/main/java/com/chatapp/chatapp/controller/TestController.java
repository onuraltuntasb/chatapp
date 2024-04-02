package com.chatapp.chatapp.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class TestController {

    @PostMapping("/api/test/someday")
    public ResponseEntity<Boolean> logout() {
        //TODO localhost data
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/")
    public String home() {
        //TODO localhost data
        return "return hello";
    }

    @GetMapping("/secured")
    public String secured() {
        //TODO localhost data
    return "hello, secured!";
    }

    @GetMapping("/profile")
    public Map<String, Object> getUser(@AuthenticationPrincipal OAuth2User oAuth2User) {



        //if login successful then
        //are there details of user in db if true then
        //just update jwt token
        //if false save user and jwt details

        //when JSESSION is finished in 1h
        //then go with jwt token get user details

        //login type is OAuth or email/password


        return oAuth2User.getAttributes();
    }

}
