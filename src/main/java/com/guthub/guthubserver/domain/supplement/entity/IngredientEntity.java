package com.guthub.guthubserver.domain.supplement.entity;

import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Ingredients")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientEntity extends BaseEntity {

    String name;
}
