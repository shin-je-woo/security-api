package com.jewoos.securityapi.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuth2UserInfo {

    private final String providerId;
    private final String email;
    private final String name;
}
