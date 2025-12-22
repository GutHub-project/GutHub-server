package com.guthub.guthubserver.domain.gut.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MonthlyGutHealthResponseDto {

    private List<DailyStatus> statusList;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DailyStatus {
        private String date; // "2025-12-01"
        private String status; // "GOOD", "NORMAL", "BAD"
        private Integer badCount; // nullable
        private String violationReason; // nullable
        private LocalDateTime updatedAt; // nullable
    }
}
