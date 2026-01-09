package com.guthub.guthubserver.domain.diet.repository;

import com.guthub.guthubserver.domain.diet.document.FoodDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FoodSearchRepository extends ElasticsearchRepository<FoodDocument, Long> {
    // 기본 CRUD 메서드 자동 제공
}
