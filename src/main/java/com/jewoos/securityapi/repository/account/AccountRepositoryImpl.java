package com.jewoos.securityapi.repository.account;

import com.jewoos.securityapi.security.service.AccountDetails;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.jewoos.securityapi.entity.QAccount.account;
import static com.jewoos.securityapi.entity.QAccountRole.accountRole;
import static com.jewoos.securityapi.entity.QRole.role;

@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    public Optional<AccountDetails> findByUserIdWithRole(String userId) {
        JPAQuery<AccountDetails> query = queryFactory
                .select(Projections.fields(AccountDetails.class,
                        account.userId,
                        account.password,
                        role.roleType))
                .from(account)
                .join(accountRole).fetchJoin()
                .join(role).fetchJoin();

        return Optional.ofNullable(query.fetchOne());
    }
}
