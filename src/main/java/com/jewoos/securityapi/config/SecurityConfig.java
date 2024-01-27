package com.jewoos.securityapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewoos.securityapi.repository.account.AccountRepository;
import com.jewoos.securityapi.security.filter.ApiLoginFilter;
import com.jewoos.securityapi.security.filter.JwtAuthenticationFilter;
import com.jewoos.securityapi.security.handler.LoginFailureHandler;
import com.jewoos.securityapi.security.handler.LoginSuccessHandler;
import com.jewoos.securityapi.security.handler.NoAuthenticationHandler;
import com.jewoos.securityapi.security.handler.NoAuthorizationHandler;
import com.jewoos.securityapi.security.jwt.JwtProperties;
import com.jewoos.securityapi.security.jwt.JwtProvider;
import com.jewoos.securityapi.security.provider.ApiLoginProvider;
import com.jewoos.securityapi.security.service.AccountDetailsService;
import com.jewoos.securityapi.security.service.CustomOAuth2UserSerivce;
import com.jewoos.securityapi.security.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;
    private final AccountRepository accountRepository;
    private final RedisService redisService;

    @Bean
    public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login*", "signup*", "/token*", "/error*").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService())))
                .addFilterBefore(apiLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JwtAuthenticationFilter(objectMapper, jwtProvider()), SecurityContextHolderFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(new NoAuthenticationHandler(objectMapper))
                        .accessDeniedHandler(new NoAuthorizationHandler(objectMapper)));

        return http.getOrBuild();
    }

    @Bean
    public ApiLoginFilter apiLoginFilter() {
        ApiLoginFilter apiLoginFilter = new ApiLoginFilter(objectMapper);
        apiLoginFilter.setAuthenticationSuccessHandler(new LoginSuccessHandler(objectMapper, jwtProvider(), jwtProperties));
        apiLoginFilter.setAuthenticationFailureHandler(new LoginFailureHandler(objectMapper));
        apiLoginFilter.setAuthenticationManager(authenticationManager());
        apiLoginFilter.setSecurityContextRepository(new NullSecurityContextRepository()); // JWT는 stateless특징
        return apiLoginFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        ApiLoginProvider apiLoginProvider = new ApiLoginProvider(userDetailsService(), passwordEncoder());
        return new ProviderManager(apiLoginProvider);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new AccountDetailsService(accountRepository);
    }

    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProvider(jwtProperties, userDetailsService(), redisService);
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new CustomOAuth2UserSerivce(accountRepository);
    }

}
