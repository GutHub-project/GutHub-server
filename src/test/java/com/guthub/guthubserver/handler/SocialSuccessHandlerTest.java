package com.guthub.guthubserver.handler;

import com.guthub.guthubserver.domain.gut.entity.GutType;
import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.domain.user.dto.CustomOAuth2User;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.domain.user.entity.UserRoleType;
import com.guthub.guthubserver.domain.user.repository.UserRepository;
import com.guthub.guthubserver.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialSuccessHandlerTest {

    @InjectMocks
    private SocialSuccessHandler successHandler;

    @Mock
    private JwtService jwtService;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    private static final String FRONTEND_URL = "http://frontend.test";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(successHandler, "frontendUrl", FRONTEND_URL);
    }

    /**
     * 신규 사용자 → /profile-setup + tempToken
     */
    @Test
    void 신규_사용자면_프로필_설정_페이지로_리디렉션한다() throws Exception {
        // given
        String username = "oauth-user";

        CustomOAuth2User oAuth2User = createOAuth2User(username);

        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(userRepository.findByUsernameAndIsLock(username, false))
                .thenReturn(Optional.empty());

        when(jwtUtil.createJWT(username, "TEMP", true))
                .thenReturn("temp.jwt.token");

        // when
        successHandler.onAuthenticationSuccess(
                new MockHttpServletRequest(),
                response,
                authentication
        );

        // then
        assertThat(response.getRedirectedUrl())
                .startsWith(FRONTEND_URL + "/profile-setup")
                .contains("tempToken=temp.jwt.token");

        verify(jwtUtil).createJWT(username, "TEMP", true);
        verify(jwtService, never()).addRefresh(any(), any());
    }

    /**
     * 기존 사용자 + gutType 있음 → /login-success
     */
    @Test
    void 기존_사용자면_로그인_성공_페이지로_리디렉션한다() throws Exception {
        // given
        String username = "existing-user";

        CustomOAuth2User oAuth2User = createOAuth2User(username);

        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());

        MockHttpServletResponse response = new MockHttpServletResponse();

        UserEntity user = mock(UserEntity.class);
        when(user.getUsername()).thenReturn(username);
        when(user.getRoleType()).thenReturn(UserRoleType.USER);
        when(user.getGutType()).thenReturn(GutType.builder().build());

        when(userRepository.findByUsernameAndIsLock(username, false))
                .thenReturn(Optional.of(user));

        when(jwtUtil.createJWT(username, "USER", true))
                .thenReturn("access.jwt");

        when(jwtUtil.createJWT(username, "USER", false))
                .thenReturn("refresh.jwt");

        // when
        successHandler.onAuthenticationSuccess(
                new MockHttpServletRequest(),
                response,
                authentication
        );

        // then
        assertThat(response.getRedirectedUrl())
                .startsWith(FRONTEND_URL + "/login-success")
                .contains("accessToken=access.jwt");

        verify(jwtService).addRefresh(username, "refresh.jwt");
        assertThat(response.getCookies()).isNotEmpty();
    }

    /**
     * DB에 있지만 gutType 없음 → 신규 사용자 처리
     */
    @Test
    void gutType이_null이면_신규_사용자로_처리한다() throws Exception {
        // given
        String username = "temp-user";

        CustomOAuth2User oAuth2User = createOAuth2User(username);

        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken(oAuth2User, null, oAuth2User.getAuthorities());

        MockHttpServletResponse response = new MockHttpServletResponse();

        UserEntity user = mock(UserEntity.class);

        when(user.getGutType()).thenReturn(null);

        when(userRepository.findByUsernameAndIsLock(username, false))
                .thenReturn(Optional.of(user));

        // when
        successHandler.onAuthenticationSuccess(
                new MockHttpServletRequest(),
                response,
                authentication
        );

        // then
        assertThat(response.getRedirectedUrl())
                .contains("/profile-setup");

    }

    private CustomOAuth2User createOAuth2User(String username) {
        return new CustomOAuth2User(
                Map.of(
                        "sub", username,
                        "email", username + "@test.com"
                ),
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                username
        );
    }
}
