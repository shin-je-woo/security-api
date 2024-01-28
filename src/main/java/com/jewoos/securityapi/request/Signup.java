package com.jewoos.securityapi.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Signup {

    private final String userId;
    private final String password;
    private final String email;

    @Builder
    public Signup(String userId, String password, String email) {
        this.userId = userId;
        this.password = password;
        this.email = email;
    }
}
