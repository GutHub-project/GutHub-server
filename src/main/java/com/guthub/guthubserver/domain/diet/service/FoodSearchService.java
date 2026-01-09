package com.guthub.guthubserver.domain.diet.service;

import com.guthub.guthubserver.domain.diet.document.FoodDocument;
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
public class FoodSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 키워드로 음식 검색 (nori 분석기 활용 한글 형태소 검색)
     * 
     * @param keyword 검색 키워드
     * @return 검색 결과 목록
     */
    public List<FoodDocument> searchFoods(String keyword) {
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("name")
                                .query(keyword)))
                .withPageable(PageRequest.of(0, 20))
                .build();

        SearchHits<FoodDocument> searchHits = elasticsearchOperations.search(nativeQuery, FoodDocument.class);
        return searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }
}
