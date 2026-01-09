package com.guthub.guthubserver.domain.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guthub.guthubserver.domain.search.dto.ProductSearchResponseDto;
import com.guthub.guthubserver.domain.search.dto.SupplementSearchItemDto;
import com.guthub.guthubserver.domain.supplement.document.SupplementDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper objectMapper;

    private static final int PAGE_SIZE = 10;

    /**
     * 키워드로 건기식 검색 (search_after 기반 커서 페이지네이션)
     */
    public ProductSearchResponseDto searchProducts(String keyword, String cursor) {
        // 커서 디코딩
        Object[] searchAfterValues = decodeCursor(cursor);

        // ES 쿼리 생성
        NativeQuery query = buildSearchQuery(keyword, searchAfterValues);

        // 검색 실행
        SearchHits<SupplementDocument> searchHits = elasticsearchOperations.search(query, SupplementDocument.class);

        // 결과 변환
        List<SupplementSearchItemDto> items = searchHits.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // 다음 페이지 커서 생성
        String nextCursor = null;
        boolean hasNext = false;

        if (!searchHits.getSearchHits().isEmpty() && searchHits.getSearchHits().size() == PAGE_SIZE) {
            SearchHit<SupplementDocument> lastHit = searchHits.getSearchHits()
                    .get(searchHits.getSearchHits().size() - 1);
            List<Object> sortValues = lastHit.getSortValues();
            if (!sortValues.isEmpty()) {
                nextCursor = encodeCursor(sortValues);
                hasNext = true;
            }
        }

        return ProductSearchResponseDto.builder()
                .query(keyword)
                .supplementList(items)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    private NativeQuery buildSearchQuery(String keyword, Object[] searchAfterValues) {
        var queryBuilder = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("name")
                                .query(keyword)))
                .withSort(s -> s
                        .field(f -> f
                                .field("totalRankingScore")
                                .order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)))
                .withPageable(PageRequest.of(0, PAGE_SIZE));

        if (searchAfterValues != null && searchAfterValues.length > 0) {
            queryBuilder.withSearchAfter(List.of(searchAfterValues));
        }

        return queryBuilder.build();
    }

    private SupplementSearchItemDto toDto(SearchHit<SupplementDocument> hit) {
        SupplementDocument doc = hit.getContent();
        return SupplementSearchItemDto.builder()
                .supplementId(doc.getId())
                .supplementName(doc.getName())
                .imageUrl(doc.getImageUrl())
                .avgRating(doc.getTotalRatingAverage())
                .cntReview(doc.getTotalReviewCount())
                .brand(doc.getBrand())
                .price(doc.getPrice())
                .build();
    }

    private String encodeCursor(List<Object> sortValues) {
        try {
            Map<String, Object> cursorData = Map.of("sort", sortValues);
            String json = objectMapper.writeValueAsString(cursorData);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            log.error("Failed to encode cursor", e);
            return null;
        }
    }

    private Object[] decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }

        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);
            String json = new String(decoded, StandardCharsets.UTF_8);
            Map<String, Object> cursorData = objectMapper.readValue(json, Map.class);
            List<Object> sortValues = (List<Object>) cursorData.get("sort");
            return sortValues != null ? sortValues.toArray() : null;
        } catch (Exception e) {
            log.error("Failed to decode cursor: {}", cursor, e);
            throw new IllegalArgumentException("cursor가 유효하지 않습니다.");
        }
    }
}
