package com.jewoos.securityapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewoos.securityapi.request.Signin;
import com.jewoos.securityapi.security.token.AccountToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public ApiLoginFilter(ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher("/login", "POST"));
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        Signin signin = objectMapper.readValue(request.getReader(), Signin.class);
        if (!StringUtils.hasText(signin.getUserId()) || !StringUtils.hasText(signin.getPassword()))
            throw new IllegalArgumentException("userId or password is not valid");

        AccountToken accountToken = new AccountToken(signin.getUserId(), signin.getPassword());

        return getAuthenticationManager().authenticate(accountToken);
    }
}
