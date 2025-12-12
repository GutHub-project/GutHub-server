package com.guthub.guthubserver.domain.diet.dto;

import com.guthub.guthubserver.domain.diet.entity.MealType;
import com.guthub.guthubserver.domain.gut.entity.OverallGutHealthStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class DailyDietSummaryResponseDto {
    private LocalDate date;
    private Map<MealType, List<DietLogResponseDto>> categorizedDietLogs;
    private TotalNutrientInfo totalNutrientInfo;
    private GutHealthAnalysis gutHealthAnalysis;

    @Getter
    @Builder
    public static class TotalNutrientInfo {
        private float totalCalories;
        private float totalDietaryFiber;
        private float totalProbiotics;
        private float totalSaturatedFat;
        private float totalSugar;
        private float totalRefinedCarbs;
        private float totalFlour;
    }

    @Getter
    @Builder
    public static class GutHealthAnalysis {
        private OverallGutHealthStatus overallStatus;
        private List<NutrientComparison> comparisons;

        @Getter
        @Builder
        public static class NutrientComparison {
            private String nutrientName;
            private float dailyIntake;
            private Float minLimit;
            private Float maxLimit;
            private NutrientStatus status;
            private boolean isExceeded;
            private boolean isBelowMin;
        }
    }
}
