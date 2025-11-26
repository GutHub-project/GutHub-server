package com.guthub.guthubserver.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.global.dto.ApiResponse;
import com.guthub.guthubserver.global.util.CookieUtil;
import com.guthub.guthubserver.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Qualifier("LoginSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginSuccessHandler(JwtService jwtService, JWTUtil jwtUtil) {
        this.jwtService = jwtService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        String accessToken = jwtUtil.createJWT(username, role, true);
        String refreshToken = jwtUtil.createJWT(username, role, false);

        jwtService.addRefresh(username, refreshToken);

        // Refresh Token은 HttpOnly 쿠키로 설정
        CookieUtil.addRefreshTokenCookie(response, refreshToken);

        // Access Token은 JSON 응답 본문으로 전달
        Map<String, String> accessTokenMap = Collections.singletonMap("accessToken", accessToken);
        ApiResponse<Map<String, String>> apiResponse = ApiResponse.of("SUCCESS", "로그인이 완료되었습니다.", accessTokenMap);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.getWriter().flush();
    }
}
