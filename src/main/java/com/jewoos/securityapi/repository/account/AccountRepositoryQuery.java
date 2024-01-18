package com.jewoos.securityapi.repository.account;

import com.jewoos.securityapi.entity.Account;

import java.util.Optional;

public interface AccountRepositoryQuery {

    Optional<Account> findAllWithRole(String userId);
}
