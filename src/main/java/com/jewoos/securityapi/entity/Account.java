package com.jewoos.securityapi.entity;

import com.jewoos.securityapi.request.Signup;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_account_id",
                columnNames = {"account_id"}),
        @UniqueConstraint(name = "uk_email",
                columnNames = {"email"})
})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String userId;

    @Column(nullable = false, length = 50)
    private String email;

    private String password;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AccountRole> accountRoles = new ArrayList<>();

    @Builder
    public Account(String userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public static Account createAccount(Signup signup, AccountRole... accountRoles) {
        Account account = Account.builder()
                .userId(signup.getUserId())
                .password(signup.getPassword())
                .email(signup.getEmail())
                .build();

        for (AccountRole accountRole : accountRoles) {
            accountRole.setAccount(account);
            account.getAccountRoles().add(accountRole);
        }

        return account;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        if(CollectionUtils.isEmpty(this.accountRoles)) return Collections.emptySet();
        return this.accountRoles.stream()
                .map(AccountRole::getRole)
                .collect(Collectors.toSet());
    }
}
