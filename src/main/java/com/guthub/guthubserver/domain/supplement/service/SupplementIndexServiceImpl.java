package com.guthub.guthubserver.domain.supplement.service;


import com.guthub.guthubserver.domain.supplement.document.SupplementDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplementIndexServiceImpl implements SupplementIndexService {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void updateRankingFields(SupplementDocument doc) {
        // 1. 업데이트할 필드만 Map에 담습니다.
        Map<String, Object> updates = new HashMap<>();
        updates.put("totalReviewCount", doc.getTotalReviewCount());
        updates.put("totalRatingAverage", doc.getTotalRatingAverage());
        updates.put("totalRankingScore", doc.getTotalRankingScore());
        updates.put("lastReviewDate", doc.getLastReviewDate());
        updates.put("updatedAt", doc.getUpdatedAt());

        // Nested 필드인 gutTypeStats는 리스트 전체를 교체합니다.
        updates.put("gutTypeStats", doc.getGutTypeStats());

        // 2. Document 객체로 변환
        Document document = Document.from(updates);

        // 3. UpdateQuery 생성
        // withDocAsUpsert(false): 문서가 없으면 업데이트 실패 (상품 정보가 없는 상태에서 통계만 넣을 순 없으므로)
        UpdateQuery updateQuery = UpdateQuery.builder(doc.getId().toString())
                .withDocument(document)
                .withDocAsUpsert(false)
                .build();

        // 4. ES에 반영
        try {
            elasticsearchOperations.update(updateQuery, IndexCoordinates.of("supplements"));
        } catch (Exception e) {
            log.error("Failed to update ranking fields for supplement id: {}", doc.getId(), e);
            // 필요 시 여기서 DLQ로 보내거나 재시도 로직 추가
        }
    }
}