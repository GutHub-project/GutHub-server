package com.guthub.guthubserver.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    // 7일 (초 단위)
    private static final int REFRESH_TOKEN_EXPIRATION_SECONDS = 60 * 60 * 24 * 7;

    public static void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // TODO: HTTPS 적용 시 true로 변경
        cookie.setPath("/");
        cookie.setMaxAge(REFRESH_TOKEN_EXPIRATION_SECONDS);
        response.addCookie(cookie);
    }

    public static void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // TODO: HTTPS 적용 시 true로 변경
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
