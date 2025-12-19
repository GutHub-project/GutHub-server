package com.guthub.guthubserver.domain.gut.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Nutrient {
    DIETARY_FIBER("식이섬유"),
    PROBIOTICS("프로바이오틱스"),
    SATURATED_FAT("포화지방"),
    SUGAR("당"),
    REFINED_CARBS("정제탄수화물"),
    FLOUR("밀가루");

    private final String displayName;

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
