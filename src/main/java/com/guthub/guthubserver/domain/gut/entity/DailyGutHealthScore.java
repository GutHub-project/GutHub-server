package com.guthub.guthubserver.domain.gut.entity;

import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint; // 추가
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "daily_gut_health_scores", uniqueConstraints = { // uniqueConstraints 추가
    @UniqueConstraint(
        name = "uk_user_record_date",
        columnNames = {"user_id", "record_date"}
    )
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyGutHealthScore extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "record_date", nullable = false) // unique = true 제거
    private LocalDate recordDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "overall_status", nullable = false, length = 20)
    private OverallGutHealthStatus overallStatus;

    public void updateOverallStatus(OverallGutHealthStatus overallStatus) {
        this.overallStatus = overallStatus;
    }
}
