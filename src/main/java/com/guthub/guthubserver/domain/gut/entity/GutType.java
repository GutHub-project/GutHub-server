package com.guthub.guthubserver.domain.gut.entity;

import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "gut_types")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
