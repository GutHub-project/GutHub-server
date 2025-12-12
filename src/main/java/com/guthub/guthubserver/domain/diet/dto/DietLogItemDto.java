package com.guthub.guthubserver.domain.diet.dto;

import com.guthub.guthubserver.domain.diet.entity.MealType; // 추가
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DietLogItemDto {

    @NotBlank(message = "Food name cannot be blank")
    private String foodName;

    private Float amount; // Can be null, default is 1.0f

    @NotNull(message = "Meal type cannot be blank") // NotBlank 대신 NotNull 사용 (enum은 null만 체크)
    private MealType mealType; // String에서 MealType으로 변경
}
