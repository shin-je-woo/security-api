package com.jewoos.securityapi.controller;

import com.jewoos.securityapi.response.TokenResponse;
import com.jewoos.securityapi.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtProvider jwtProvider;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestParam String refreshToken) {
        TokenResponse tokenResponse = jwtProvider.reIssueToken(refreshToken);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }
}
