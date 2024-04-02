package com.chatapp.chatapp.security;


import com.chatapp.chatapp.dao.UserDao;
import com.chatapp.chatapp.exception.ResourceNotFoundException;
import com.chatapp.chatapp.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDao userDao;
    private final JwtUtils jwtUtils;


    public Optional<String> readServletCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            return Arrays
                    .stream(cookies)
                    .filter(cookie -> name.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findAny();
        } return Optional.empty();
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        Optional<String> egAuthCookie = readServletCookie(request, "eg-auth-cookie"); String userEmail;
        String jwtToken = ""; if (egAuthCookie.isPresent()) {
            userEmail = egAuthCookie.get().substring(egAuthCookie.get().indexOf("email") + 6);
            jwtToken = egAuthCookie.get().substring(0, egAuthCookie.get().indexOf("email"));

        } else {
            userEmail = ""; filterChain.doFilter(request, response); return;
        }


        log.info("userEmail : {}", userEmail); if (!userEmail.equals("") &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = (UserDetails) userDao.findByEmail(userEmail);
            if(userDetails == null){
                throw new ResourceNotFoundException("User not found with this email : "+ userEmail);
            }

            if (jwtUtils.isTokenValid(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                                                                                                        userDetails.getAuthorities()
                ); authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } filterChain.doFilter(request, response);
    }
}
