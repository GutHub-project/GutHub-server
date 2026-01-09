package com.guthub.guthubserver.domain.search.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 검색 결과 내 개별 건기식 아이템 DTO
 */
@Getter
@Builder
public class SupplementSearchItemDto {

    private String supplementId;
    private String supplementName;
    private String imageUrl;
    private Double avgRating;
    private Long cntReview;
    private String brand;
    private Integer price;
}
