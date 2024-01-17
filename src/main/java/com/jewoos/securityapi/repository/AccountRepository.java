package com.jewoos.securityapi.repository;

import com.jewoos.securityapi.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryQuery {
}
