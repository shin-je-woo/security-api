package com.jewoos.securityapi.service;

import com.jewoos.securityapi.entity.Account;
import com.jewoos.securityapi.entity.AccountRole;
import com.jewoos.securityapi.entity.Role;
import com.jewoos.securityapi.enums.RoleType;
import com.jewoos.securityapi.repository.account.AccountRepository;
import com.jewoos.securityapi.repository.role.RoleRepository;
import com.jewoos.securityapi.request.Signup;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(Signup signup) {

        Role role = roleRepository.findByRoleType(RoleType.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("USER권한이 존재하지 않습니다."));

        AccountRole accountRole = AccountRole.createAccountRole(role);
        Account account = Account.createAccount(signup, accountRole);
        account.changePassword(passwordEncoder.encode(signup.getPassword()));

        accountRepository.save(account);
    }
}
