package com.jewoos.securityapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewoos.securityapi.repository.AccountRepository;
import com.jewoos.securityapi.security.filter.ApiLoginFilter;
import com.jewoos.securityapi.security.provider.ApiLoginProvider;
import com.jewoos.securityapi.security.service.AccountDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final AccountRepository accountRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login*", "signup*").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(apiLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);

        return http.getOrBuild();
    }
    @Bean
    public ApiLoginFilter apiLoginFilter() {
        ApiLoginFilter apiLoginFilter = new ApiLoginFilter(objectMapper);
//        apiLoginFilter.setAuthenticationSuccessHandler();
//        apiLoginFilter.setAuthenticationFailureHandler();
        apiLoginFilter.setAuthenticationManager(authenticationManager());
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
}
