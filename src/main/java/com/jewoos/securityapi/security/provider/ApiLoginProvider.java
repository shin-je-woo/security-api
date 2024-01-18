package com.jewoos.securityapi.security.provider;


import com.jewoos.securityapi.security.service.AccountDetails;
import com.jewoos.securityapi.security.token.AccountToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class ApiLoginProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AccountDetails accountDetails = (AccountDetails) userDetailsService.loadUserByUsername(authentication.getName());

        // 사용자가 인증요청한 패스워드와 사용자의 PW(DB에 저장)와 다르면 exception
        if (!passwordEncoder.matches(authentication.getCredentials().toString(), accountDetails.getPassword())) {
            throw new BadCredentialsException("BadCredentialsException");
        }

        return new AccountToken(accountDetails, accountDetails.getPassword(), accountDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(AccountToken.class);
    }
}
