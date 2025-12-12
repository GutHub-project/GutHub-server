package com.guthub.guthubserver.domain.gutTypes.entity;

import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "gut_nutrient_standards")
public class GutNutrientStandard extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gut_type_id")
    private GutType gutType;

    private String nutrient_name; // 영양소 이름

    private Float max_limit; // 최대 섭취 허용량 (FLOAT)

    private Float min_limit; // 최소 섭취 권장량 (FLOAT)
}
