package com.jewoos.securityapi.repository;

import com.jewoos.securityapi.security.service.AccountDetails;

import java.util.Optional;

public interface AccountRepositoryQuery {

    Optional<AccountDetails> findByUserIdWithRole(String userId);
}
