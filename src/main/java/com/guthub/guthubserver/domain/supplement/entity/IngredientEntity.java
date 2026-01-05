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
@Table(name = "ingredients")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
}
