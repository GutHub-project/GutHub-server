package com.guthub.guthubserver.auth;

import com.guthub.guthubserver.domain.jwt.entity.RefreshEntity;
import com.guthub.guthubserver.domain.jwt.repository.RefreshRepository;
import com.guthub.guthubserver.util.JWTUtil;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class JwtRefreshIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RefreshRepository refreshRepository;

    @Test
    void refreshAPI_shouldReturnNewAccessToken_andRotateRefreshToken() throws Exception {

        // given
        String username = "testuser";
        String role = "ROLE_USER";

        // 기존 refreshToken 생성 및 DB 저장
        String oldRefreshToken = jwtUtil.createJWT(username, role, false);

        refreshRepository.save(
                RefreshEntity.builder()
                        .username(username)
                        .refresh(oldRefreshToken)
                        .build()
        );

        // when & then
        mockMvc.perform(
                        post("/jwt/refresh")
                                .with(csrf())
                                .cookie(new Cookie("refreshToken", oldRefreshToken))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(cookie().exists("refreshToken"));  // rotated refresh token
    }
}
