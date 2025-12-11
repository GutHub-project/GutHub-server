package com.guthub.guthubserver.domain.diet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class DailySummaryResponseDto {

    private final LocalDate date;
    private final String gutHealthStatus; // e.g., "GOOD", "NORMAL", "BAD"
    private final String violationReason;
    private final Map<String, Float> totalNutrients; // e.g., {"calories": 2000, "dietaryFiber": 25}
}
