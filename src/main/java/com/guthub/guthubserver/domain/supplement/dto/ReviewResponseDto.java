package com.guthub.guthubserver.domain.supplement.dto;

import com.guthub.guthubserver.domain.supplement.entity.ReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {

    private Long reviewId;
    private Long supplementId;
    private String writerNickname;
    private Integer rating;
    private Integer deliveryRating;
    private String content;
    private LocalDateTime createdAt;

    public static ReviewResponseDto fromEntity(ReviewEntity entity) {
        return ReviewResponseDto.builder()
                .reviewId(entity.getId())
                .supplementId(entity.getSupplement().getId())
                .writerNickname(entity.getUser().getNickname())
                .rating(entity.getRating())
                .deliveryRating(entity.getDeliveryRating())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
