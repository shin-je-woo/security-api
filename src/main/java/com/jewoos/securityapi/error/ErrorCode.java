package com.jewoos.securityapi.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // TOKEN
    EXPIRED_TOKEN("T001", 401, "만료된 토큰입니다."),
    INVALID_TOKEN("T002", 401, "잘못된 토큰입니다."),

    // ACCOUNT
    INVALID_ID_PW("A001", 400, "아이디 혹은 비밀번호가 올바르지 않습니다."),
    NO_AUTHENTICATION("A002", 401, "인증되지 않은 사용자입니다."),
    NO_AUTHORIZATION("A003", 403, "접근권한이 없습니다."),

    // GLOBAL
    GLOBAL_EXCEPTION("G001", 500, "서버에서 에러가 발생하였습니다."),
    NO_DATA("G002", 400, "데이터가 존재하지 않습니다.");

    private final String code;
    private final int status;
    private final String message;
}
