package com.jewoos.securityapi.security.jwt;

import com.jewoos.securityapi.error.ErrorCode;
import com.jewoos.securityapi.error.GlobalException;
import com.jewoos.securityapi.response.TokenResponse;
import com.jewoos.securityapi.security.service.AccountDetails;
import com.jewoos.securityapi.security.service.RedisService;
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
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;
    private final RedisService redisService;
    private final static String DELIMITER_AUTHORITIES = ",";
    private final static String PREFIX_REFRESH_TOKEN = "refreshToken:";

    public String createAccessToken(Authentication authentication) {
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
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessExpirationTime()))
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

    /**
     * refreshToken 을 발급한다.
     * refreshToken 은 uuid 로 발급하고 refreshToken 저장소(Redis)에 저장한다.
     * Redis 에는 value 자료형으로 refreshToken:uuid userId 와 같은 방식으로 저장된다.
     *
     * @param userId refreshToken 에 해당하는 사용자 ID
     * @return 발급된 refreshToken
     */
    public String createRefreshToken(String userId) {
        String refreshToken = UUID.randomUUID().toString();
        redisService.put(PREFIX_REFRESH_TOKEN + refreshToken, userId, jwtProperties.getRefreshExpirationTime());
        return refreshToken;
    }

    /**
     * 새로운 accessToken, refreshToken 을 발급한다.
     * 기존의 refreshToken 은 만료 처리한다.
     *
     * @param refreshToken 발급가능 여부를 판단할 refreshToken
     * @return 새로운 토큰객체
     * @throws GlobalException refreshToken 저장소(Redis)에 refreshToken 이 존재하지 않을 경우 발생
     */
    public TokenResponse reIssueToken(String refreshToken) {
        String userId = getUserIdFromStorage(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        AccountToken accountToken = new AccountToken(userDetails, "", userDetails.getAuthorities());
        String accessToken = createAccessToken(accountToken);
        String reIssuedRefreshToken = createRefreshToken(userId);
        redisService.delete(PREFIX_REFRESH_TOKEN + refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(reIssuedRefreshToken)
                .build();
    }

    private String getUserIdFromStorage(String refreshToken) {
        if (!redisService.exists(PREFIX_REFRESH_TOKEN + refreshToken))
            throw new GlobalException(ErrorCode.EXPIRED_TOKEN);
        return redisService.get(PREFIX_REFRESH_TOKEN + refreshToken);
    }
}
