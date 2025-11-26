package com.guthub.guthubserver.api;

import com.guthub.guthubserver.domain.jwt.dto.AccessTokenResponseDto;
import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.global.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtController {

    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Access Token 재발급 API
     * 요청에 포함된 Refresh Token 쿠키를 검증하여 새로운 Access Token을 발급합니다.
     */
    @PostMapping("/jwt/refresh")
    public ResponseEntity<ApiResponse<AccessTokenResponseDto>> refreshAccessToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        AccessTokenResponseDto newAccessToken = jwtService.reissueAccessToken(request, response);
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "Access Token이 재발급되었습니다.", newAccessToken));
    }
}
