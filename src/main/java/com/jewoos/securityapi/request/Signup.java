package com.jewoos.securityapi.request;

import lombok.Getter;

@Getter
public class Signup {

    private String userId;
    private String password;
    private String email;
}
