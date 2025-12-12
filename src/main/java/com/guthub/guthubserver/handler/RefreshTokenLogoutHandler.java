package com.guthub.guthubserver.handler;

import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.Arrays;
import java.util.Optional;

public class RefreshTokenLogoutHandler implements LogoutHandler {

    private final JwtService jwtService;
    private final JWTUtil jwtUtil;
    private final String refreshTokenCookieName = "refreshToken";

    public RefreshTokenLogoutHandler(JwtService jwtService, JWTUtil jwtUtil) {
        this.jwtService = jwtService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 쿠키에서 Refresh 토큰 가져오기
        String refreshToken = getRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            return;
        }

        // Refresh 토큰 유효성 검증
        if (!jwtUtil.isValid(refreshToken, false)) {
            return;
        }

        // Refresh 토큰 삭제
        jwtService.removeRefresh(refreshToken);

        // Refresh 토큰 쿠키 삭제
        deleteRefreshTokenCookie(response);
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> refreshTokenCookieName.equals(cookie.getName()))
                .findFirst();

        return refreshTokenCookie.map(Cookie::getValue).orElse(null);
    }

    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie(refreshTokenCookieName, null);
        refreshTokenCookie.setMaxAge(0); // 즉시 삭제
        refreshTokenCookie.setPath("/"); // 쿠키가 적용되는 경로 설정 (전체 경로에 적용)
        refreshTokenCookie.setHttpOnly(true);
        response.addCookie(refreshTokenCookie);
    }
}
