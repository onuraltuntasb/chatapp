package com.chatapp.chatapp.controller;

import com.chatapp.chatapp.dao.RoleDao;
import com.chatapp.chatapp.dao.UserDao;
import com.chatapp.chatapp.entity.Role;
import com.chatapp.chatapp.entity.User;
import com.chatapp.chatapp.payload.request.LoginUserRequest;
import com.chatapp.chatapp.payload.request.TokenRefreshRequest;
import com.chatapp.chatapp.payload.request.UserRequest;
import com.chatapp.chatapp.payload.response.UserAuthResponse;
import com.chatapp.chatapp.security.JwtUtils;
import com.chatapp.chatapp.service.RefreshTokenService;
import com.chatapp.chatapp.service.UserService;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/api/user")
public class UserController {

    //TODO return httpOnly cookie for tokens later

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final RoleDao roleDao;
    private final UserDao userDao;
    JwtUtils jwtUtils = new JwtUtils();


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest userRequest, boolean alreadyRegistered) {

        UserAuthResponse userAuthResponse = userService.setUserOtherParams(userService.registerUser(userRequest,
                                                                                                    true,
                                                                                                    alreadyRegistered
                                                                                                   ), true,
                                                                           "register", alreadyRegistered
                                                                          );

        userAuthResponse.setIsAuth(true);

        return ResponseEntity.ok().body(userAuthResponse);


    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginUserRequest loginUserRequest) {

        UserAuthResponse userAuthResponse = userService.setUserOtherParams(userService.loginUser(loginUserRequest), true, "login"
                , true);

        userAuthResponse.setIsAuth(true);

        return ResponseEntity.ok().body(userAuthResponse);

    }

    @GetMapping("/logout-password")
    public ResponseEntity<?> logout() {
            //TODO delete localhost data
            return ResponseEntity.ok().body(true);
    }

    @GetMapping("/logout-oauth")
    public ResponseEntity<?> logout(String loginType, HttpServletRequest request, HttpServletResponse response) {
            //TODO delete localhost google oauth and tokens data
        return ResponseEntity.ok().body(true);
    }


    //TODO ip restriction and http secure using
    //TODO nonce solutions is more secure with timestamp and redis cache. It's Complex but need for real security.

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {

        Object response = null;

        String headerPayload = jwtUtils.decodeJWTToken(request.getRefreshToken());
        String[] hp = headerPayload.split(" ");
        String payload = hp[1];
        JsonObject jsonPayload = JsonParser.parseString(payload).getAsJsonObject();


        User user = userDao.findByEmail(String.valueOf(jsonPayload.get("email")));
        if (user != null) {
            if (user.getLoginType().equals("oauth2")) {
                //redirect to login page in client side
                response = ResponseEntity.ok("oauth2");
            } else if (user.getLoginType().equals("password")) {
                response = ResponseEntity.ok(refreshTokenService.createTokensFromRefresh(request.getEmail(),
                                                                                         request.getEmail()
                                                                                        ));
            }
        } else {
            response = false;
        }

        return ResponseEntity.ok(response);

    }

    @GetMapping("/check")
    public ResponseEntity<?> checkAuth(@Valid @RequestBody String token) {

        //TODO client side access refresh token cycle

        Boolean response = false;

        try {
            response = jwtUtils.validateSecretAndExpiration(token);
        } catch (Exception e) {
            response = false;
            log.info(e.getMessage());
        }
        return ResponseEntity.ok(response);

    }


    @GetMapping("/register-oauth")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal OAuth2User oAuth2User) {

        //TODO if system admin gives admin role to this user later. When refresh token is done,register

        String email = (String) oAuth2User.getAttributes().get("email");

        User user = userDao.findByEmail(email);
        boolean alreadyRegistered = false;

        if (user != null) {
            alreadyRegistered = true;
        }

        UserRequest userRequest = null;
        List<Role> roles = null;

        //if username is less than 6 characters, join "chatapp" for google oauth2. User can change username later.
        String givenName = Objects.requireNonNull(oAuth2User.getAttribute("given_name")).toString();
        userRequest = UserRequest.builder()
                                 .name(givenName.length() < 6 ?
                                               givenName + "chatapp" : givenName)
                                 .email((String) oAuth2User.getAttribute("email"))
                                 .status(User.UserStatus.ACTIVE)
                                 .loginType("oauth2")
                                 .roles(Arrays.asList(roleDao.findByName("ROLE_USER")))
                                 .adminCreationSecret("")
                                 //to guarantee password necessities + "2Osn#LMRxu"
                                 .password(oAuth2User.getAttribute("nonce") + "2Osn#LMRxu")
                                 .build();


        return registerUser(userRequest, alreadyRegistered);
    }
}
