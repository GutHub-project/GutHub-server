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

    // Elasticsearch FoodDocument에서 생성하는 생성자
    public FoodSearchResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
