package com.jewoos.securityapi.service;

import com.jewoos.securityapi.entity.Account;
import com.jewoos.securityapi.entity.AccountRole;
import com.jewoos.securityapi.entity.Role;
import com.jewoos.securityapi.enums.RoleType;
import com.jewoos.securityapi.repository.account.AccountRepository;
import com.jewoos.securityapi.repository.role.RoleRepository;
import com.jewoos.securityapi.request.Signup;
import com.jewoos.securityapi.security.oauth2.OAuth2UserInfo;
import com.jewoos.securityapi.security.service.AccountDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Account signup(Signup signup) {

        Role userRole = roleRepository.findByRoleType(RoleType.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("USER권한이 존재하지 않습니다."));
        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN권한이 존재하지 않습니다."));

        AccountRole accountRoleUser = AccountRole.createAccountRole(userRole);
        AccountRole accountRoleAdmin = AccountRole.createAccountRole(adminRole);
        Account account = Account.createAccount(signup, accountRoleUser, accountRoleAdmin);
        account.changePassword(passwordEncoder.encode(signup.getPassword()));

        return accountRepository.save(account);
    }

    @Transactional
    public AccountDetails getOrSignupOAuth2User(OAuth2UserInfo oAuth2User) {
        Account account = accountRepository.findByEmail(oAuth2User.getEmail())
                .orElseGet(() -> {
                    Signup newUser = Signup.builder()
                            .userId(oAuth2User.getName())
                            .password(UUID.randomUUID().toString())
                            .email(oAuth2User.getEmail())
                            .build();
                    return signup(newUser);
                });
        List<SimpleGrantedAuthority> authorities = account.getRoles().stream()
                .map(Role::getRoleType)
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new AccountDetails(account.getUserId(), account.getPassword(), authorities);
    }
}
