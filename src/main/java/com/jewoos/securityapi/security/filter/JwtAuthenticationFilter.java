package com.jewoos.securityapi.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewoos.securityapi.error.ErrorCode;
import com.jewoos.securityapi.error.ErrorResponse;
import com.jewoos.securityapi.security.jwt.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jws = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(jws)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Authentication authentication = jwtProvider.resolveToken(jws);
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            responseError(response, ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            responseError(response, ErrorCode.INVALID_TOKEN);
        }
    }

    private void responseError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
