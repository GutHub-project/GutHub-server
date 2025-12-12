package com.guthub.guthubserver.domain.diet.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GutHealthStreakResponseDto {
    private int streakCount;
    private String lastRecordDate; // 마지막으로 기록된 날짜 (선택 사항)
}
