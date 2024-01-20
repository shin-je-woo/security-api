package com.jewoos.securityapi.security.jwt;

import com.jewoos.securityapi.security.service.AccountDetails;
import com.jewoos.securityapi.security.token.AccountToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final static String DELIMITER_AUTHORITIES = ",";

    public String generateToken(Authentication authentication, long expirationTime) {
        String userId = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(DELIMITER_AUTHORITIES));

        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .claim("uid", userId)
                .claim("auth", authorities)
                .issuer("jewoos.site")
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    public Authentication resolveToken(String jws) {
        Jws<Claims> claims = parseJws(jws);
        UserDetails userdetails = getUserdetails(claims);
        return new AccountToken(userdetails, "", userdetails.getAuthorities());
    }

    private UserDetails getUserdetails(Jws<Claims> claims) {
        String userId = claims.getPayload().get("uid").toString();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.getPayload().get("auth").toString().split(DELIMITER_AUTHORITIES))
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new AccountDetails(userId, "", authorities);
    }

    private Jws<Claims> parseJws(String jws) {
        Jws<Claims> claims;
        try {
             claims = Jwts.parser().verifyWith(jwtProperties.getSecretKey()).build().parseSignedClaims(jws);
        } catch (JwtException e) {
            log.info("jws 파싱 중 에러발생! {} {}", e.getClass(), e.getMessage());
            throw e;
        }
        return claims;
    }
}
