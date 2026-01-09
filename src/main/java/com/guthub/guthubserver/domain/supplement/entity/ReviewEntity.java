package com.guthub.guthubserver.domain.supplement.entity;

import com.guthub.guthubserver.domain.gut.entity.GutType;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reviews")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplement_id", nullable = false)
    private SupplementEntity supplement;

    @Column(nullable = false)
    private Integer rating; // 1 ~ 5 점

    @Column(name = "delivery_rating")
    private Integer deliveryRating; // 배송 만족도 (선택)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 리뷰 작성 당시의 사용자 상태 스냅샷
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gut_type_id")
    private GutType gutTypeSnapshot;

    @Column(name = "age_snapshot")
    private Integer ageSnapshot;
}
