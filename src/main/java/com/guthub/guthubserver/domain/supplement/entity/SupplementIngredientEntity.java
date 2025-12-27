package com.guthub.guthubserver.domain.supplement.entity;

import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "supplement_ingredients")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplementIngredientEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplement_id", nullable = false)
    private SupplementEntity supplement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private IngredientEntity ingredient;

    // 추후 함량(mg) 등의 추가 정보가 필요할 경우 컬럼 추가 가능
    // private Double amount;
}
