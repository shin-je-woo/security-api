package com.jewoos.securityapi.repository.account;

import com.jewoos.securityapi.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryQuery {

    Optional<Account> findByEmail(String email);
}
