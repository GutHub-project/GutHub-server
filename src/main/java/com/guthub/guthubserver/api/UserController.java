package com.guthub.guthubserver.api;

import com.guthub.guthubserver.domain.user.dto.ProfileUpdateDto;
import com.guthub.guthubserver.domain.user.dto.ProfileResponseDto;
import com.guthub.guthubserver.domain.user.dto.UserRequestDTO;
import com.guthub.guthubserver.domain.user.dto.UserResponseDTO;
import com.guthub.guthubserver.domain.user.service.UserService;
import com.guthub.guthubserver.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "User API", description = "사용자 정보 관련 API")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping(value = "/user/exist", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> existUserApi(
            @Validated(UserRequestDTO.existGroup.class) @RequestBody UserRequestDTO dto
    ) {
        boolean isAvailable = !userService.existUser(dto);
        String message = isAvailable ? "사용 가능한 아이디입니다." : "이미 사용중인 아이디입니다.";
        Map<String, Boolean> data = Collections.singletonMap("isAvailable", isAvailable);
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", message, data));
    }


    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Long>>> joinApi (
            @Validated(UserRequestDTO.addGroup.class) @RequestBody UserRequestDTO dto
    ) {
        Long id = userService.addUser(dto);
        Map<String, Long> data = Collections.singletonMap("userEntityId", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("SUCCESS", "회원가입이 완료되었습니다.", data));
    }



    @GetMapping(value = "/user")
    public ResponseEntity<ApiResponse<UserResponseDTO>> userMeApi() {
        UserResponseDTO user = userService.readUser();
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "내 정보 조회 성공", user));
    }


    @PutMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Long>>> updateUserApi(
            @Validated(UserRequestDTO.updateGroup.class) @RequestBody UserRequestDTO dto
    ) throws AccessDeniedException {
        Long id = userService.updateUser(dto);
        Map<String, Long> data = Collections.singletonMap("userEntityId", id);
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "내 정보 수정 성공", data));
    }


    @DeleteMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Void>> deleteUserApi(
            @Validated(UserRequestDTO.deleteGroup.class) @RequestBody UserRequestDTO dto
    ) throws AccessDeniedException {
        userService.deleteUser(dto);
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "회원 탈퇴 성공"));
    }

    @PutMapping(value = "/user/profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Long>>> updateProfileApi(
            @Validated @RequestBody ProfileUpdateDto dto
    ) throws AccessDeniedException {
        Long id = userService.updateProfile(dto);
        Map<String, Long> data = Collections.singletonMap("userEntityId", id);
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "프로필 정보 수정 성공", data));
    }

    @GetMapping(value = "/user/profile")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> userProfileApi() {
        ProfileResponseDto userProfile = userService.readUserProfile();
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "내 프로필 정보 조회 성공", userProfile));
    }
}
