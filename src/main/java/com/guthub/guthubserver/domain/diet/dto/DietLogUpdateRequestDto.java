package com.guthub.guthubserver.domain.diet.dto;

import com.guthub.guthubserver.domain.diet.entity.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DietLogUpdateRequestDto {

    // id 필드 제거

    @NotBlank(message = "Food name cannot be blank")
    private String foodName;

    private Float amount; // Can be null, default is 1.0f

    @NotNull(message = "Meal type cannot be null")
    private MealType mealType;
}
