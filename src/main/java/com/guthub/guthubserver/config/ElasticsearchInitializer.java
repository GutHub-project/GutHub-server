package com.guthub.guthubserver.config;

import com.guthub.guthubserver.domain.diet.service.FoodIndexService;
import com.guthub.guthubserver.domain.supplement.service.SupplementIndexService;
import com.guthub.guthubserver.domain.supplement.service.SupplementStatsAggregator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * local, dev 환경에서 애플리케이션 시작 시 Elasticsearch에 데이터를 자동 인덱싱합니다.
 * - 음식 데이터: MySQL → ES 전체 동기화
 * - 건기식 데이터: 기본 정보 인덱싱 → 리뷰 통계 집계 업데이트
 */
@Slf4j
@Component
@Profile({ "local", "dev" })
@RequiredArgsConstructor
public class ElasticsearchInitializer implements ApplicationRunner {

    private final FoodIndexService foodIndexService;
    private final SupplementIndexService supplementIndexService;
    private final SupplementStatsAggregator supplementStatsAggregator;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting automatic Elasticsearch indexing...");

        // 1. 음식 데이터 인덱싱
        try {
            log.info("Indexing food data...");
            foodIndexService.indexAllFoods();
            log.info("Food data indexing completed successfully.");
        } catch (Exception e) {
            log.error("Failed to index food data: {}", e.getMessage(), e);
        }

        // 2. 건기식 기본 정보 인덱싱 (먼저 실행해야 통계 업데이트가 동작함)
        try {
            log.info("Indexing supplement base data...");
            supplementIndexService.indexAllSupplements();
            log.info("Supplement base data indexing completed successfully.");
        } catch (Exception e) {
            log.error("Failed to index supplement base data: {}", e.getMessage(), e);
        }

        // 3. 건기식 통계 데이터 업데이트 (기존 문서에 리뷰 통계 추가)
        try {
            log.info("Updating supplement stats data...");
            supplementStatsAggregator.aggregateAndIndexAll();
            log.info("Supplement stats update completed successfully.");
        } catch (Exception e) {
            log.error("Failed to update supplement stats: {}", e.getMessage(), e);
        }

        log.info("Elasticsearch indexing process finished.");
    }
}
