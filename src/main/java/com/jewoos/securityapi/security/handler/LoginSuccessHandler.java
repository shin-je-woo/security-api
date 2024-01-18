package com.jewoos.securityapi.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewoos.securityapi.response.TokenResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("{} 사용자가 로그인에 성공했습니다.", authentication.getName());

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(authentication.getName())
                .refreshToken("리프레쉬 토큰")
                .build();

        String tokenResponseJson = objectMapper.writeValueAsString(tokenResponse);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.getWriter().write(tokenResponseJson);
    }
}
