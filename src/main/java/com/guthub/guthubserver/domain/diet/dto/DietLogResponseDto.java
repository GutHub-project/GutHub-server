package com.guthub.guthubserver.domain.diet.dto;

import com.guthub.guthubserver.domain.diet.entity.DietLog;
import com.guthub.guthubserver.domain.diet.entity.MealType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DietLogResponseDto {

    private final Long dietLogId; // logId를 dietLogId로 변경
    private final Long foodId;
    private final String foodName;
    private final LocalDate logDate;
    private final Float amount;
    private final MealType mealType;

    public DietLogResponseDto(DietLog dietLog) {
        this.dietLogId = dietLog.getId(); // logId를 dietLogId로 변경
        this.foodId = dietLog.getFood().getId();
        this.foodName = dietLog.getFood().getName();
        this.logDate = dietLog.getLogDate();
        this.amount = dietLog.getAmount();
        this.mealType = dietLog.getMealType();
    }
}
