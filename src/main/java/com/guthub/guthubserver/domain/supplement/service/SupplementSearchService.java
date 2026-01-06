package com.guthub.guthubserver.domain.supplement.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
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
     * Search by product name or ingredients using Multi-match query.
     * Strictly follows Spring Data Elasticsearch 5.x+ & Java API Client syntax.
     */
    public List<SupplementDocument> searchSupplements(String keyword) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .fields("name", "ingredients")
                                .query(keyword)
                        )
                )
                .withPageable(PageRequest.of(0, 20))
                .build();

        SearchHits<SupplementDocument> searchHits = elasticsearchOperations.search(nativeQuery, SupplementDocument.class);
        return searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    /**
     * Get ranking by GutType using Nested Sorting.
     * Sorts supplements by the rating average specific to the given gut type.
     */
    public List<SupplementDocument> getRankingByGutType(String gutTypeCode) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.matchAll(m -> m))
                .withSort(s -> s
                        .field(f -> f
                                .field("gutTypeStats.ratingAverage")
                                .order(SortOrder.Desc)
                                .nested(n -> n
                                        .path("gutTypeStats")
                                        .filter(fq -> fq
                                                .term(t -> t
                                                        .field("gutTypeStats.gutTypeCode")
                                                        .value(gutTypeCode)
                                                )
                                        )
                                )
                        )
                )
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<SupplementDocument> searchHits = elasticsearchOperations.search(nativeQuery, SupplementDocument.class);
        return searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }
}
