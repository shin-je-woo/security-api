package com.jewoos.securityapi.security.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum OAuthAttributes {

    NAVER(response -> new OAuth2UserInfo(
            response.get("id").toString(),
            response.get("email").toString(),
            response.get("name").toString()
    ));

    private final Function<Map<String, Object>, OAuth2UserInfo> getOAuth2UserInfo;

    public static OAuth2UserInfo ofResponse(String registrationId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> provider.name().toLowerCase().equals(registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .getOAuth2UserInfo.apply(attributes);
    }
}
