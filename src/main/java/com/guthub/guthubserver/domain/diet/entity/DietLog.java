package com.guthub.guthubserver.domain.diet.entity;

import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "diet_logs")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(columnDefinition = "FLOAT DEFAULT 1.0")
    private Float amount;

    @Enumerated(EnumType.STRING) // 추가
    @Column(name = "meal_type", length = 20)
    private MealType mealType; // String에서 MealType으로 변경

    // --- 업데이트 메소드 추가 ---
    public void updateFood(Food food) {
        this.food = food;
    }

    public void updateAmount(Float amount) {
        this.amount = amount;
    }

    public void updateMealType(MealType mealType) { // 파라미터 타입 변경
        this.mealType = mealType;
    }
}
