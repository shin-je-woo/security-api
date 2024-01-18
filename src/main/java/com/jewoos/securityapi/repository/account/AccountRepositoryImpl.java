package com.jewoos.securityapi.repository.account;

import com.jewoos.securityapi.entity.Account;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.jewoos.securityapi.entity.QAccount.account;
import static com.jewoos.securityapi.entity.QAccountRole.accountRole;
import static com.jewoos.securityapi.entity.QRole.role;

@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    public Optional<Account> findAllWithRole(String userId) {
        Account result = queryFactory
                .selectFrom(account)
                .join(account.accountRoles, accountRole).fetchJoin()
                .join(accountRole.role, role).fetchJoin()
                .where(account.userId.eq(userId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
