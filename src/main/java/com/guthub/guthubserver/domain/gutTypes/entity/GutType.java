package com.guthub.guthubserver.domain.gutTypes.entity;

import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "gut_types")
public class GutType extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl; // VARCHAR(255)


    // 이 유형에 해당하는 영양소 기준 관계 (GutType : GutNutrientStandard = 1:N)
    @OneToMany(mappedBy = "gutType")
    private List<GutNutrientStandard> nutrientStandards;
}
