package com.guthub.guthubserver.domain.diet.dto;

import com.guthub.guthubserver.domain.diet.entity.Food;
import lombok.Getter;

@Getter
public class FoodSearchResponseDto {
    private final Long id;
    private final String name;

    public FoodSearchResponseDto(Food food) {
        this.id = food.getId();
        this.name = food.getName();
    }
}
