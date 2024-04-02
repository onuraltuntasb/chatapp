package com.chatapp.chatapp.service;

import com.chatapp.chatapp.dao.RefreshTokenDao;
import com.chatapp.chatapp.dao.RoleDao;
import com.chatapp.chatapp.dao.UserDao;
import com.chatapp.chatapp.entity.RefreshToken;
import com.chatapp.chatapp.payload.request.LoginUserRequest;
import com.chatapp.chatapp.security.JwtUtils;

import com.chatapp.chatapp.entity.User;
import com.chatapp.chatapp.exception.ResourceNotFoundException;
import com.chatapp.chatapp.payload.request.UserRequest;
import com.chatapp.chatapp.payload.response.UserAuthResponse;
import com.chatapp.chatapp.setup.AuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService{



    private final AuthProperties authProperties;
    private final RoleDao roleDao;
    private final UserDao userDao;
    private final RefreshTokenDao refreshTokenDao;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;


    @Transactional
    public User registerUser(UserRequest userRequest, boolean authenticated,boolean alreadyRegistered) {

        User user = new User();

        if (userRequest.getAdminCreationSecret() != null && !userRequest.getAdminCreationSecret().isEmpty() &&
                userRequest.getAdminCreationSecret().equals(authProperties.getJwtSecret())) {
            user.setRoles(Arrays.asList(roleDao.findByName("ROLE_ADMIN")));
        } else {
            user.setRoles(Arrays.asList(roleDao.findByName("ROLE_USER")));
        }


        String plainPassword = userRequest.getPassword();
        if (plainPassword != null) {
            user.setPassword(passwordEncoder.encode(plainPassword));
        } else {
            throw new ResourceNotFoundException("User password not found!");
        }

        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        user.setStatus(User.UserStatus.ACTIVE);
        user.setLoginType(userRequest.getLoginType());


        if(alreadyRegistered){
            log.info("user already registered, go setOtherParameters and update just tokens.");
            return user;
        }else{
            return userDao.save(user);
        }

    }

    public User loginUser(LoginUserRequest loginUserRequest) {

        String email = loginUserRequest.getEmail();
        String password = loginUserRequest.getPassword();

        User rUser = userDao.findByEmail(email);

        System.out.println("rUser : " +password );

        if(rUser == null){
            throw new ResourceNotFoundException("Not found email with this email : "+ email);
        }
        //email - password authentication
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserRequest.getEmail(), loginUserRequest.getPassword()
                ));

        return rUser;
    }

    public UserAuthResponse setUserOtherParams(User user, boolean authenticated, String opType,boolean alreadyRegistered) {
        User userFully = userDao.findByEmail(user.getEmail());
        if(userFully == null){
            throw new ResourceNotFoundException("Not found user with this email : "+ user.getEmail());
        }

        RefreshToken refreshToken = null;

        if (authenticated) {
            if (opType.equals("register")) {

                if(alreadyRegistered){
                    refreshToken = refreshTokenService.updateRefreshToken(userFully.getId());
                }else{
                    refreshToken = refreshTokenService
                            .createRefreshToken(userFully.getId());
                }


            } else if (opType.equals("login")) {
                refreshToken = refreshTokenDao.findTokenByUser(user);
                if(refreshToken==null){
                    throw new ResourceNotFoundException("refresh token is not found with this user : " + user.getEmail());
                }

            } else {
                throw new RuntimeException("opType is wrong");
            }

        }

        if (refreshToken == null) {
            throw new NullPointerException("Refresh token is null");
        }

        return UserAuthResponse.builder()
                               .name(user.getName())
                               .email(user.getEmail())
                               .authorities(user.getAuthorities())
                               .jwtToken(new JwtUtils().generateToken(userFully))
                               .jwtRefreshToken(refreshToken.getToken())
                               .build();
    }


}

