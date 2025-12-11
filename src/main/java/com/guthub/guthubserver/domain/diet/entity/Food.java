package com.guthub.guthubserver.domain.diet.entity;

import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "foods")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Food extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "FLOAT DEFAULT 0")
    private Float calories;

    @Column(name = "dietary_fiber", columnDefinition = "FLOAT DEFAULT 0")
    private Float dietaryFiber;

    @Column(columnDefinition = "FLOAT DEFAULT 0")
    private Float probiotics;

    @Column(name = "saturated_fat", columnDefinition = "FLOAT DEFAULT 0")
    private Float saturatedFat;

    @Column(columnDefinition = "FLOAT DEFAULT 0")
    private Float sugar;

    @Column(name = "refined_carbs", columnDefinition = "FLOAT DEFAULT 0")
    private Float refinedCarbs;

    @Column(name = "is_flour_based", columnDefinition = "TINYINT DEFAULT 0")
    private Boolean isFlourBased;

    @Column
    private Float flour;
}
