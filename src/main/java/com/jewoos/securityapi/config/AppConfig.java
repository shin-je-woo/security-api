package com.jewoos.securityapi.config;

import com.jewoos.securityapi.security.jwt.JwtProperties;
import com.jewoos.securityapi.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    @Bean
    public JwtProvider jwtProvider(JwtProperties jwtProperties) {
        return new JwtProvider(jwtProperties);
    }
}
