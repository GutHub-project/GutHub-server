package com.guthub.guthubserver.domain.supplement.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "supplements")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplementDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String name;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String brand;

    @Field(type = FieldType.Integer)
    private Integer price;

    @Field(type = FieldType.Keyword, index = false)
    private String imageUrl;

    // 검색을 위한 성분명 리스트 (예: ["프로바이오틱스", "아연"])
    @Field(type = FieldType.Text, analyzer = "nori")
    @Builder.Default
    private List<String> ingredients = new ArrayList<>();

    // 장 타입별 통계 (Nested Object)
    @Field(type = FieldType.Nested)
    @Builder.Default
    private List<GutTypeStat> gutTypeStats = new ArrayList<>();

    // 전체 통계 (기본 정렬용)
    @Field(type = FieldType.Double)
    @Builder.Default
    private Double totalRatingAverage = 0.0;

    @Field(type = FieldType.Long)
    @Builder.Default
    private Long totalReviewCount = 0L;

    @Field(type = FieldType.Double)
    @Builder.Default
    private Double totalRankingScore = 0.0;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime lastReviewDate; // 제품에 가장 최근 리뷰가 달린 시간

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedAt; // 문서가 마지막으로 배치에서 갱신된 시간

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GutTypeStat {

        @Field(type = FieldType.Keyword)
        private String gutTypeCode; // 예: "CONSTIPATION", "SENSITIVE"

        @Field(type = FieldType.Long)
        @Builder.Default
        private Long reviewCount = 0L;

        @Field(type = FieldType.Double)
        @Builder.Default
        private Double ratingAverage = 0.0;

        @Field(type = FieldType.Double)
        @Builder.Default
        private Double rankingScore = 0.0;
    }
}
