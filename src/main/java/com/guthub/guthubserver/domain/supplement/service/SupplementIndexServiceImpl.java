package com.guthub.guthubserver.domain.supplement.service;

import com.guthub.guthubserver.domain.supplement.document.SupplementDocument;
import com.guthub.guthubserver.domain.supplement.entity.SupplementEntity;
import com.guthub.guthubserver.domain.supplement.repository.SupplementRepository;
import com.guthub.guthubserver.domain.supplement.repository.SupplementSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplementIndexServiceImpl implements SupplementIndexService {

    private final SupplementRepository supplementRepository;
    private final SupplementSearchRepository supplementSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    // ==================== 전체 인덱싱 메서드 ====================

    @Override
    @Transactional(readOnly = true)
    public void indexAllSupplements() {
        List<SupplementEntity> supplements = supplementRepository.findAll();
        List<SupplementDocument> documents = supplements.stream()
                .map(this::toDocument)
                .collect(Collectors.toList());

        supplementSearchRepository.saveAll(documents);

        // 인덱싱 후 즉시 검색 가능하도록 refresh 강제 실행
        elasticsearchOperations.indexOps(SupplementDocument.class).refresh();

        log.info("Indexed {} supplements to Elasticsearch", documents.size());
    }

    @Override
    public void indexSupplement(SupplementEntity supplement) {
        SupplementDocument document = toDocument(supplement);
        supplementSearchRepository.save(document);
        log.debug("Indexed supplement: {}", supplement.getName());
    }

    @Override
    public void deleteFromIndex(Long supplementId) {
        supplementSearchRepository.deleteById(supplementId.toString());
        log.debug("Deleted supplement from index: {}", supplementId);
    }

    // ==================== 통계 부분 업데이트 메서드 ====================

    @Override
    public void updateRankingFields(SupplementDocument doc) {
        // 1. 업데이트할 필드만 Map에 담습니다.
        Map<String, Object> updates = new HashMap<>();
        updates.put("totalReviewCount", doc.getTotalReviewCount());
        updates.put("totalRatingAverage", doc.getTotalRatingAverage());
        updates.put("totalRankingScore", doc.getTotalRankingScore());

        // Instant를 epoch millis로 변환하여 저장 (ES date 포맷 호환성)
        if (doc.getLastReviewDate() != null) {
            updates.put("lastReviewDate", doc.getLastReviewDate().toEpochMilli());
        }
        if (doc.getUpdatedAt() != null) {
            updates.put("updatedAt", doc.getUpdatedAt().toEpochMilli());
        }

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
        }
    }

    // ==================== Private Helper ====================

    private SupplementDocument toDocument(SupplementEntity entity) {
        // 성분 목록 추출
        List<String> ingredients = entity.getSupplementIngredients().stream()
                .map(si -> si.getIngredient().getName())
                .collect(Collectors.toList());

        return SupplementDocument.builder()
                .id(entity.getId().toString())
                .name(entity.getName())
                .brand(entity.getBrand())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .ingredients(ingredients)
                // 통계 필드는 기본값으로 초기화 (나중에 aggregator가 업데이트)
                .totalRatingAverage(entity.getRatingAverage() != null ? entity.getRatingAverage() : 0.0)
                .totalReviewCount(entity.getReviewCount() != null ? entity.getReviewCount() : 0L)
                .build();
    }
}