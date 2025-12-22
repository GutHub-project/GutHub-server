package com.guthub.guthubserver.domain.gut.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GutHealthScoreResponseDto {

    private String status; // "GOOD", "NORMAL", "BAD"
    private Integer badCount; // nullable
    private String violationReason; // nullable
    private LocalDateTime updatedAt; // nullable
}
