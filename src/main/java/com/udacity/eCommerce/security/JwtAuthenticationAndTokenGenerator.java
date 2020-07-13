package com.udacity.eCommerce.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.eCommerce.model.requests.CreateUserRequest;
import com.udacity.eCommerce.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JwtAuthenticationAndTokenGenerator extends UsernamePasswordAuthenticationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationAndTokenGenerator.class);

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationAndTokenGenerator(AuthenticationManager authenticationManager) {
        super.setAuthenticationFailureHandler(new JWTAuthenticationFailureHandler());
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException {
        try {
            CreateUserRequest createUser = new ObjectMapper().readValue(httpServletRequest.getInputStream(), CreateUserRequest.class);
            LOG.debug("JwtAuthenticationAndTokenGenerator.attemptAuthentication Attempting authentication for user {}", createUser.getUsername());
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            createUser.getUsername(),
                            createUser.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            LOG.warn("JwtAuthenticationAndTokenGenerator.attemptAuthentication FAILURE [Authentication attempt failure] ",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest httpServletRequest,
                                            HttpServletResponse httpServletResponse,
                                            FilterChain filterChain,
                                            Authentication authentication) throws IOException, ServletException {

        String token = JWT.create()
                .withSubject(((User) authentication.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + Constants.EXPIRATION_TIME))
                .sign(HMAC512(Constants.SECRET.getBytes()));
        httpServletResponse.addHeader(Constants.HEADER_STRING, Constants.TOKEN_PREFIX + token);
        LOG.info("JwtAuthenticationAndTokenGenerator.successfulAuthentication SUCCESS [User {} authenticated, JWT issued]", ((User) authentication.getPrincipal()).getUsername());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest httpServletRequest,
                                              HttpServletResponse httpServletResponse,
                                              AuthenticationException authenticationException)
            throws IOException, javax.servlet.ServletException {
        LOG.warn("JwtAuthenticationAndTokenGenerator.unsuccessfulAuthentication  FAILURE [Authentication attempt failed. {}].", authenticationException.getMessage());
        super.unsuccessfulAuthentication(httpServletRequest, httpServletResponse, authenticationException);
    }
}
