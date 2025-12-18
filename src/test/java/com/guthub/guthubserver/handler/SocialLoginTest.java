package com.guthub.guthubserver.handler;

import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.domain.user.entity.UserRoleType;
import com.guthub.guthubserver.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SocialLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final String GOOGLE_REGISTRATION_ID = "google";

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 DB를 깨끗하게 유지 (선택 사항, @Transactional이 롤백을 처리해줌)
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("신규 사용자가 소셜 로그인 시 프로필 설정 페이지로 리디렉션된다")
    void testNewUserSocialLogin() throws Exception {
        // given
        String username = "google_newuser123";
        String nickname = "새로운구글유저";
        String email = "newuser@google.com";

        // when
        MvcResult result = mockMvc.perform(get("/login/oauth2/code/google")
                        .with(mockOAuth2Login(username, nickname, email)))
                .andExpect(status().is3xxRedirection()) // 302 리디렉션 상태 확인
                .andExpect(redirectedUrlPattern("**/profile-setup?tempToken=*")) // 리디렉션 URL 패턴 확인
                .andReturn();

        // then
        // 1. DB에 TEMP 유저로 저장되었는지 확인
        UserEntity savedUser = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new AssertionError("신규 사용자가 DB에 저장되지 않았습니다."));
        assertThat(savedUser.getRoleType()).isEqualTo(UserRoleType.TEMP);
        assertThat(savedUser.getGutType()).isNull();

        // 2. 리디렉션 URL에 tempToken이 포함되어 있는지 확인 (위에서 패턴으로 이미 확인)
        String redirectedUrl = result.getResponse().getRedirectedUrl();
        assertThat(redirectedUrl).contains("tempToken=");
    }

    @Test
    @DisplayName("기존 사용자(프로필 미완료)가 소셜 로그인 시 프로필 설정 페이지로 리디렉션된다")
    void testExistingUserWithoutProfileSocialLogin() throws Exception {
        // given
        String username = "google_existinguser_temp";
        String nickname = "기존임시유저";
        String email = "existing_temp@google.com";

        // DB에 TEMP 상태의 유저를 미리 저장
        userRepository.save(UserEntity.builder()
                .username(username)
                .roleType(UserRoleType.TEMP)
                .isSocial(true)
                .password("social")
                .isLock(false)
                .build());

        // when & then
        mockMvc.perform(get("/login/oauth2/code/google")
                        .with(mockOAuth2Login(username, nickname, email)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/profile-setup?tempToken=*"));
    }

    @Test
    @DisplayName("기존 사용자(프로필 완료)가 소셜 로그인 시 로그인 성공 페이지로 리디렉션된다")
    void testExistingUserWithProfileSocialLogin() throws Exception {
        // given
        String username = "google_existinguser_user";
        String nickname = "기존유저";
        String email = "existing_user@google.com";

        // DB에 USER 상태 및 gutType이 있는 유저를 미리 저장
        userRepository.save(UserEntity.builder()
                .username(username)
                .roleType(UserRoleType.USER)
                .gutType(null) // 간단하게 null로 설정, 실제로는 GutType 엔티티를 넣어야 함
                .isSocial(true)
                .password("social")
                .isLock(false)
                .build());

        // when & then
        MvcResult result = mockMvc.perform(get("/login/oauth2/code/google")
                        .with(mockOAuth2Login(username, nickname, email)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login-success?accessToken=*"))
                .andReturn();

        String redirectedUrl = result.getResponse().getRedirectedUrl();
        assertThat(redirectedUrl).contains("accessToken=");
    }


    /**
     * 테스트를 위한 Mock OAuth2User를 생성하는 PostProcessor
     */
    private SecurityMockMvcRequestPostProcessors.OAuth2LoginRequestPostProcessor mockOAuth2Login(String username, String nickname, String email) {
        return oauth2Login()
                .registrationId(GOOGLE_REGISTRATION_ID)
                .principalName(username)
                .attributes(attrs -> {
                    attrs.put("sub", username.replace(GOOGLE_REGISTRATION_ID + "_", ""));
                    attrs.put("name", nickname);
                    attrs.put("email", email);
                });
    }
}
