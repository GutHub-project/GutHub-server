package com.guthub.guthubserver.domain.diet.dto;

import com.guthub.guthubserver.domain.diet.entity.DietLog;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public class DietLogResponseDto {

    private final Long id;
    private final Long foodId;
    private final String foodName;
    private final LocalDate logDate;
    private final LocalTime logTime;
    private final Float amount;
    private final String mealType;

    public DietLogResponseDto(DietLog dietLog) {
        this.id = dietLog.getId();
        this.foodId = dietLog.getFood().getId();
        this.foodName = dietLog.getFood().getName();
        this.logDate = dietLog.getLogDate();
        this.logTime = dietLog.getLogTime();
        this.amount = dietLog.getAmount();
        this.mealType = dietLog.getMealType();
    }
}
