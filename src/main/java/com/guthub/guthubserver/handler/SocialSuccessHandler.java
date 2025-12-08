package com.guthub.guthubserver.handler;

import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.global.util.CookieUtil;
import com.guthub.guthubserver.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Qualifier("SocialSuccessHandler")
public class SocialSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final JWTUtil jwtUtil;
    private final String frontendUrl;


    public SocialSuccessHandler(JwtService jwtService, JWTUtil jwtUtil, @Value("${frontend.url}") String frontendUrl) {
        this.jwtService = jwtService;
        this.jwtUtil = jwtUtil;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        String refreshToken = jwtUtil.createJWT(username, role, false);

        jwtService.addRefresh(username, refreshToken);

        // Refresh Token을 HttpOnly 쿠키로 설정
        CookieUtil.addRefreshTokenCookie(response, refreshToken);


        String redirectUrl = frontendUrl.endsWith("/")
                ? frontendUrl + "login/success"
                : frontendUrl + "/login/success";

        response.sendRedirect(redirectUrl);
    }
}
