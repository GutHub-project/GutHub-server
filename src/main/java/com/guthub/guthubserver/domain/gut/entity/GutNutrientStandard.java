package com.guthub.guthubserver.domain.gut.entity;

import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "gut_nutrient_standards")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GutNutrientStandard extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gut_type_id")
    private GutType gutType;

    @Enumerated(EnumType.STRING)
    @Column(name = "nutrient_name")
    private Nutrient nutrientName; // String에서 Nutrient Enum으로 변경

    @Column(name = "max_limit")
    private Float maxLimit; // max_limit으로 변경

    @Column(name = "min_limit")
    private Float minLimit; // min_limit으로 변경
}
