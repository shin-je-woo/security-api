package com.jewoos.securityapi.service;

import com.jewoos.securityapi.dto.request.Signup;
import com.jewoos.securityapi.entity.Account;
import com.jewoos.securityapi.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(Signup signup) {
        Account account = Account.builder()
                .userId(signup.getUserId())
                .password(passwordEncoder.encode(signup.getPassword()))
                .email(signup.getEmail())
                .build();

        accountRepository.save(account);
    }
}
