package com.guthub.guthubserver.domain.user.dto;

import com.guthub.guthubserver.domain.gut.entity.GutType;
import lombok.Getter;

@Getter
public class GutTypeInProfileDto {
    private final String name;
    private final String code;
    private final String description;
    private final String imageUrl;

    public GutTypeInProfileDto(GutType entity) {
        this.name = entity.getName();
        this.code = entity.getCode();
        this.description = entity.getDescription();
        this.imageUrl = entity.getImageUrl();
    }
}
