package com.guthub.guthubserver.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.util.JWTUtil;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StreamUtils;

public class RefreshTokenLogoutHandler implements LogoutHandler {

    private final JwtService jwtService;
    private final JWTUtil jwtUtil;
    private final String refreshTokenParameter = "refreshToken";

    public RefreshTokenLogoutHandler(JwtService jwtService, JWTUtil jwtUtil) {
        this.jwtService = jwtService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        Map<String, String> refreshMap;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            refreshMap = objectMapper.readValue(messageBody, new TypeReference<>(){
            });

            String refreshToken = refreshMap.get(refreshTokenParameter);

            // 유효성 검증
            if (refreshToken == null) {
                return;
            }
            Boolean isValid = jwtUtil.isValid(refreshToken, false);
            if (!isValid) {
                return;
            }

            // Refresh 토큰 삭제
            jwtService.removeRefresh(refreshToken);

        } catch (IOException e) {
            throw new RuntimeException("Failed : 리프레시 토큰을 읽기");
        }


    }

}
