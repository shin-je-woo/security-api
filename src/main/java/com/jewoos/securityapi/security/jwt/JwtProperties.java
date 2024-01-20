package com.jewoos.securityapi.security.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;

@Validated
@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "my-jwt")
public class JwtProperties {

    @NotEmpty
    private String secret;

    private long accessExpirationTime;

    private long refreshExpirationTime;

    private SecretKey secretKey;

    @PostConstruct
    private void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
    }
}
