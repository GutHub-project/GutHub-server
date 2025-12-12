package com.guthub.guthubserver.domain.user.dto;

import com.guthub.guthubserver.domain.user.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfileUpdateDto(
        @NotBlank(message = "별명은 필수 입력값입니다.")
        String nickname,

        @NotNull(message = "연령대는 필수 입력값입니다.")
        Integer ageRange,

        @NotNull(message = "성별은 필수 입력값입니다.")
        Gender gender,

        @NotBlank(message = "장 건강 타입은 필수 입력값입니다.")
        String gutType
) {
}
