package com.guthub.guthubserver.domain.supplement.repository;

import com.guthub.guthubserver.domain.supplement.document.SupplementDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SupplementSearchRepository extends ElasticsearchRepository<SupplementDocument, String> {
    // 기본 검색 메서드 정의 가능
    // 예: List<SupplementDocument> findByNameContaining(String name);
}
