package com.guthub.guthubserver.domain.supplement.entity;

import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "supplements")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplementEntity extends BaseEntity {

    private String name;
    private String brand;
    private Integer price;
    private String imageUrl;
    private String description;
    private String purchaseUrl;
    private Integer capacity;

    @OneToMany(mappedBy = "supplement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SupplementIngredientEntity> supplementIngredients = new ArrayList<>();
}
