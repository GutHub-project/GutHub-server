package com.guthub.guthubserver.domain.jwt.service;

import com.guthub.guthubserver.domain.jwt.dto.AccessTokenResponseDto;
import com.guthub.guthubserver.domain.jwt.entity.RefreshEntity;
import com.guthub.guthubserver.domain.jwt.repository.RefreshRepository;
import com.guthub.guthubserver.global.util.CookieUtil;
import com.guthub.guthubserver.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JwtService {

    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    public JwtService(RefreshRepository refreshRepository, JWTUtil jwtUtil) {
        this.refreshRepository = refreshRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AccessTokenResponseDto reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String oldRefreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    oldRefreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (oldRefreshToken == null) {
            throw new IllegalArgumentException("Refresh token이 존재하지 않습니다.");
        }

        if (!jwtUtil.isValid(oldRefreshToken, false) || !refreshRepository.existsByRefresh(oldRefreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh token입니다.");
        }

        String username = jwtUtil.getUsername(oldRefreshToken);
        String role = jwtUtil.getRole(oldRefreshToken);

        String newAccessToken = jwtUtil.createJWT(username, role, true);
        String newRefreshToken = jwtUtil.createJWT(username, role, false);

        // 기존 Refresh Token 삭제 및 새 Refresh Token 저장 (Rotation)
        refreshRepository.deleteByRefresh(oldRefreshToken);
        addRefresh(username, newRefreshToken);

        // 새 Refresh Token을 쿠키로 설정
        CookieUtil.addRefreshTokenCookie(response, newRefreshToken);

        return new AccessTokenResponseDto(newAccessToken);
    }

    @Transactional
    public void addRefresh(String username, String refreshToken) {
        RefreshEntity entity = RefreshEntity.builder()
                .username(username)
                .refresh(refreshToken)
                .build();
        refreshRepository.save(entity);
    }

    @Transactional
    public void removeRefresh(String refreshToken) {
        refreshRepository.deleteByRefresh(refreshToken);
    }

    @Transactional
    public void removeRefreshUser(String username) {
        refreshRepository.deleteByUsername(username);
    }
}
