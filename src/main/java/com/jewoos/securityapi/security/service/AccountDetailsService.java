package com.jewoos.securityapi.security.service;

import com.jewoos.securityapi.entity.Account;
import com.jewoos.securityapi.entity.AccountRole;
import com.jewoos.securityapi.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Account account = accountRepository.findAllWithRole(userId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        List<SimpleGrantedAuthority> authorities = account.getAccountRoles().stream()
                .map(AccountRole::getRole)
                .map(role -> role.getRoleType().name())
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new AccountDetails(account.getUserId(), account.getPassword(), authorities);
    }
}
