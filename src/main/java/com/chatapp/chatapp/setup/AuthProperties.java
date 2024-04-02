package com.chatapp.chatapp.setup;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private String jwtSecret = "secretKey";
    private String jwtExpirationMs = "300000";  // 5min
    private String jwtRefreshExpirationSecond = "1800"; // 30m
    private String adminSecret = "ydSR&R*07I";



}