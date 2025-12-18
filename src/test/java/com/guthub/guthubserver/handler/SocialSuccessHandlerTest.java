package com.guthub.guthubserver.handler;

import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.domain.user.repository.UserRepository;
import com.guthub.guthubserver.util.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SocialSuccessHandlerTest {

    @Test
    void successHandler_shouldSetRefreshTokenCookie_andRedirect() throws Exception {

        // given
        JwtService jwtService = mock(JwtService.class);
        JWTUtil jwtUtil = mock(JWTUtil.class);
        UserRepository userRepository = mock(UserRepository.class);
        String frontendUrl = "http://localhost:8080";

        // handler
        SocialSuccessHandler handler =
                new SocialSuccessHandler(jwtService, jwtUtil, userRepository );

        // OAuth2User principal 구성
        var oAuth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of(
                        "sub", "google_123456",
                        "email", "test@example.com"
                ),
                "sub" // nameAttributeKey
        );

        Authentication authentication = new OAuth2AuthenticationToken(
                oAuth2User,
                oAuth2User.getAuthorities(),
                "google"
        );

        // JWTUtil mocking
        when(jwtUtil.createJWT("google_123456", "ROLE_USER", false))
                .thenReturn("REFRESH_TOKEN_ABC");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        handler.onAuthenticationSuccess(request, response, authentication);

        // then — redirect URL
        assertThat(response.getRedirectedUrl())
                .isEqualTo("http://localhost:8080/login/success");

        // then — refreshToken 쿠키 존재
        var cookie = response.getCookie("refreshToken");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo("REFRESH_TOKEN_ABC");
        assertThat(cookie.isHttpOnly()).isTrue();

        // then — DB 저장 검증
        verify(jwtService, times(1))
                .addRefresh("google_123456", "REFRESH_TOKEN_ABC");
    }
}
