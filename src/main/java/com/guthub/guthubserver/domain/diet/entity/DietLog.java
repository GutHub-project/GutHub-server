package com.guthub.guthubserver.domain.diet.entity;

import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "log_time", nullable = false)
    private LocalTime logTime;

    @Column(columnDefinition = "FLOAT DEFAULT 1.0")
    private Float amount;

    @Column(name = "meal_type", length = 20)
    private String mealType;
}
