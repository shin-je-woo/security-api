package com.jewoos.securityapi.security.service;

import com.jewoos.securityapi.repository.account.AccountRepository;
import com.jewoos.securityapi.security.oauth2.OAuth2UserInfo;
import com.jewoos.securityapi.security.oauth2.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;

@RequiredArgsConstructor
public class CustomOAuth2UserSerivce extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = OAuthAttributes.ofResponse(registrationId, oAuth2User.getAttribute("response"));

        // TODO: provider로부터 받아온 사용자정보로 회원가입/로그인처리 후 accessToken발급 필요

        return new AccountDetails("qweqwe", "qweqwe", Collections.emptyList());
    }
}
