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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column
    private Integer price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "purchase_url")
    private String purchaseUrl;

    @Column
    private Integer capacity;

    // 랭킹 및 정렬을 위한 통계 필드 (Review 등록/삭제 시 업데이트)
    @Column(name = "review_count")
    @Builder.Default
    private Long reviewCount = 0L;

    @Column(name = "rating_average")
    @Builder.Default
    private Double ratingAverage = 0.0;

    @OneToMany(mappedBy = "supplement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SupplementIngredientEntity> supplementIngredients = new ArrayList<>();

    // 비즈니스 로직: 리뷰 추가 시 평점/개수 업데이트
    public void updateRating(Double newRatingAverage, Long newReviewCount) {
        this.ratingAverage = newRatingAverage;
        this.reviewCount = newReviewCount;
    }
}
