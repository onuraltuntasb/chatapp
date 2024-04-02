package com.chatapp.chatapp.service;

import com.chatapp.chatapp.dao.RefreshTokenDao;
import com.chatapp.chatapp.dao.UserDao;
import com.chatapp.chatapp.entity.User;
import com.chatapp.chatapp.exception.ResourceNotFoundException;
import com.chatapp.chatapp.exception.TokenCustomException;
import com.chatapp.chatapp.payload.response.TokenRefreshResponse;
import com.chatapp.chatapp.security.JwtUtils;
import com.chatapp.chatapp.setup.AuthProperties;
import com.chatapp.chatapp.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

    AuthProperties authProperties = new AuthProperties();

    private String jwtRefreshExpirationSecond = authProperties.getJwtRefreshExpirationSecond();
    private final UserDao userDao;
    private final RefreshTokenDao refreshTokenDao;


    public TokenRefreshResponse createTokensFromRefresh(String email, String refreshToken) {

        JwtUtils jwtUtils = new JwtUtils();
        TokenRefreshResponse tokenRefreshResponse = null;
        if (jwtUtils.isTokenValid(refreshToken, email)) {

            User user = userDao.findByEmail(email);
            String rAccessToken = jwtUtils.generateToken(user);
            RefreshToken rRefreshToken = createRefreshToken(userDao.findByEmail(email).getId());
            tokenRefreshResponse = new TokenRefreshResponse(rAccessToken,
                                                                                 rRefreshToken.toString());

        } else {
            throw new TokenCustomException(refreshToken, "Token is not valid!");
        }
        return tokenRefreshResponse;
    }

    //check refreshtoken is valid
    //check refreshtoken time is done
    //create new refresh and access token save to db
    //return with response

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        User user = userDao.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("user not found with this id : " + userId);
        }

        refreshToken.setUser_id(user.getId());

        refreshToken.setExpiryDate(Instant.now().plusSeconds(Long.parseLong(jwtRefreshExpirationSecond)));
        refreshToken.setToken(UUID.randomUUID().toString());


        refreshToken = refreshTokenDao.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken updateRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        User user = userDao.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("user not found with this id : " + userId);
        }

        refreshToken.setUser_id(user.getId());

        refreshToken.setExpiryDate(Instant.now().plusSeconds(Long.parseLong(jwtRefreshExpirationSecond)));
        refreshToken.setToken(UUID.randomUUID().toString());


        refreshToken = refreshTokenDao.updateByUserId(userId,refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenDao.deleteByToken(token);

            throw new TokenCustomException(token.getToken(),
                                           "Refresh token was expired. Please make a new sign in request"
            );
        }

        return token;
    }

}
