package com.guthub.guthubserver.domain.supplement.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.guthub.guthubserver.domain.supplement.document.SupplementDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplementSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 제품명 또는 성분명으로 검색
     */
    public List<SupplementDocument> searchSupplements(String keyword) {
        Query query = QueryBuilders.multiMatch(m -> m
                .fields("name", "ingredients")
                .query(keyword)
        );

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(PageRequest.of(0, 20))
                .build();

        SearchHits<SupplementDocument> searchHits = elasticsearchOperations.search(nativeQuery, SupplementDocument.class);
        return searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    /**
     * 특정 장 타입(GutType) 사용자들에게 인기 있는 랭킹 조회
     * 정렬 기준: 해당 장 타입 유저들의 평점 평균 DESC
     */
    public List<SupplementDocument> getRankingByGutType(String gutTypeCode) {
        // Nested Sort: gutTypeStats 리스트 중 gutTypeCode가 일치하는 항목의 ratingAverage로 정렬
        SortOptions sortOptions = SortOptions.of(s -> s
                .field(f -> f
                        .field("gutTypeStats.ratingAverage")
                        .order(SortOrder.Desc)
                        .nested(n -> n
                                .path("gutTypeStats")
                                .filter(q -> q
                                        .term(t -> t
                                                .field("gutTypeStats.gutTypeCode")
                                                .value(gutTypeCode)
                                        )
                                )
                        )
                )
        );

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.matchAll(m -> m))
                .withSort(sortOptions)
                .withPageable(PageRequest.of(0, 10))
                .build();

        return elasticsearchOperations.search(nativeQuery, SupplementDocument.class)
                .stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }
}
