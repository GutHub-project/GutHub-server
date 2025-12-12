package com.guthub.guthubserver.api;

import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.domain.user.repository.UserRepository;
import com.guthub.guthubserver.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Profile({"dev", "local"}) // dev와 local 프로필 모두에서 활성화
@RestController
@RequiredArgsConstructor
public class TestController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @GetMapping("/test/token")
    public ResponseEntity<Map<String, String>> getTestToken(@RequestParam String username) {
        UserEntity user = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        // JWTUtil을 사용하여 토큰 생성
        String accessToken = jwtUtil.createJWT(user.getUsername(), user.getRoleType().name(), true); // isAccess = true
        String refreshToken = jwtUtil.createJWT(user.getUsername(), user.getRoleType().name(), false); // isAccess = false
        
        // JwtService의 addRefresh 메소드를 사용하여 리프레시 토큰 저장
        jwtService.addRefresh(username, refreshToken);

        return ResponseEntity.ok(Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken
        ));
    }
}
