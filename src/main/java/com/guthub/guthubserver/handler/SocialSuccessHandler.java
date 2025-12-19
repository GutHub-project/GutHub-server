package com.guthub.guthubserver.handler;

import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.domain.user.dto.CustomOAuth2User;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.domain.user.repository.UserRepository;
import com.guthub.guthubserver.global.util.CookieUtil;
import com.guthub.guthubserver.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@Qualifier("SocialSuccessHandler")
@RequiredArgsConstructor
public class SocialSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getName();

        Optional<UserEntity> userOptional = userRepository.findByUsernameAndIsLock(username, false);

        // 사용자가 DB에 없는 경우 (신규 사용자)
        if (userOptional.isEmpty()) {
            handleNewUserRedirect(response, username);
            return;
        }

        // 사용자가 DB에 있는 경우 (기존 사용자)
        UserEntity user = userOptional.get();
        if (user.getGutType() == null) {
            handleNewUserRedirect(response, username);
        } else {
            handleExistingUserRedirect(response, user);
        }
    }

    private void handleNewUserRedirect(HttpServletResponse response, String username) throws IOException {
        log.info("프로필 설정이 필요합니다. 프로필 설정 페이지로 리디렉션합니다. username: {}", username);

        // 프로필 설정을 위한 임시 토큰 발급 (TEMP 권한)
        String tempToken = jwtUtil.createJWT(username, "TEMP", true);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/profile-setup")
                .queryParam("tempToken", tempToken)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private void handleExistingUserRedirect(HttpServletResponse response, UserEntity user) throws IOException {
        log.info("로그인 성공. 메인 페이지로 리디렉션합니다. username: {}", user.getUsername());

        String accessToken = jwtUtil.createJWT(user.getUsername(), user.getRoleType().name(), true);
        String refreshToken = jwtUtil.createJWT(user.getUsername(), user.getRoleType().name(), false);

        jwtService.addRefresh(user.getUsername(), refreshToken);
        CookieUtil.addRefreshTokenCookie(response, refreshToken);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/login-success")
                .queryParam("accessToken", accessToken)
                .build().toUriString();
                
        response.sendRedirect(redirectUrl);
    }
}
