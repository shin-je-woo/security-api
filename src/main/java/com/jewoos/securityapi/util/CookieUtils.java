package com.jewoos.securityapi.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public final class CookieUtils {

    private CookieUtils() {
        throw new IllegalStateException("This is Utility class!");
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        if (!StringUtils.hasText(name) || request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .sameSite("none")
                .path("/")
                .maxAge(maxAge)
                .secure(true)
                .httpOnly(true)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        if (request.getCookies() == null) return;

        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .forEach(cookie -> {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                });
    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> clazz) {
        return clazz.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
