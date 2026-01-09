package com.guthub.guthubserver.domain.supplement.service;

import com.guthub.guthubserver.domain.supplement.document.SupplementDocument;
import com.guthub.guthubserver.domain.supplement.repository.ReviewRepository;
import com.guthub.guthubserver.domain.supplement.repository.SupplementAggregateProjection;
import com.guthub.guthubserver.domain.supplement.repository.SupplementGutAggregateProjection;
import com.guthub.guthubserver.domain.supplement.repository.SupplementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplementStatsAggregator {

        private final ReviewRepository reviewRepository;
        private final SupplementRepository supplementRepository;
        private final SupplementIndexService indexService;

        private static final int BATCH_SIZE = 500;

        public void aggregateAndIndexAll() {

                // 🔹 1. 전체 집계는 단 한 번만 수행
                Map<Long, SupplementAggregateProjection> totalAggMap = reviewRepository.aggregatePerSupplementAll()
                                .stream()
                                .collect(Collectors.toMap(
                                                SupplementAggregateProjection::getSupplementId,
                                                a -> a));

                Map<Long, List<SupplementGutAggregateProjection>> gutAggMap = reviewRepository
                                .aggregatePerSupplementByGutAll().stream()
                                .collect(Collectors.groupingBy(
                                                SupplementGutAggregateProjection::getSupplementId));

                int pageIdx = 0;
                Page<Long> page;

                do {
                        PageRequest pageRequest = PageRequest.of(pageIdx, BATCH_SIZE);
                        page = supplementRepository.findAllIds(pageRequest);

                        for (Long supplementId : page.getContent()) {
                                SupplementAggregateProjection agg = totalAggMap.get(supplementId);
                                List<SupplementGutAggregateProjection> gutAggs = gutAggMap.getOrDefault(supplementId,
                                                Collections.emptyList());

                                SupplementDocument doc = buildDocument(supplementId, agg, gutAggs);

                                // 🔹 랭킹 필드만 부분 업데이트
                                indexService.updateRankingFields(doc);
                        }

                        pageIdx++;
                } while (page.hasNext());
        }

        private SupplementDocument buildDocument(
                        Long supplementId,
                        SupplementAggregateProjection agg,
                        List<SupplementGutAggregateProjection> guts) {

                SupplementDocument.SupplementDocumentBuilder builder = SupplementDocument.builder()
                                .id(supplementId.toString())
                                .updatedAt(Instant.now());

                if (agg != null && agg.getReviewCount() > 0) {
                        Instant lastReviewInstant = agg.getLastReviewAt() != null
                                        ? agg.getLastReviewAt().atZone(ZoneId.systemDefault()).toInstant()
                                        : null;
                        builder
                                        .totalReviewCount(agg.getReviewCount())
                                        .totalRatingAverage(agg.getAvgRating())
                                        .lastReviewDate(lastReviewInstant)
                                        .totalRankingScore(
                                                        computeTotalRankingScore(
                                                                        agg.getAvgRating(),
                                                                        agg.getReviewCount(),
                                                                        agg.getLastReviewAt()));
                } else {
                        builder
                                        .totalReviewCount(0L)
                                        .totalRatingAverage(0.0)
                                        .totalRankingScore(0.0)
                                        .lastReviewDate(null);
                }

                List<SupplementDocument.GutTypeStat> gutStats = guts.stream()
                                .map(g -> SupplementDocument.GutTypeStat.builder()
                                                .gutTypeCode(g.getGutTypeCode())
                                                .reviewCount(g.getReviewCount())
                                                .ratingAverage(g.getAvgRating())
                                                .rankingScore(
                                                                computeGutRankingScore(
                                                                                g.getAvgRating(),
                                                                                g.getReviewCount()))
                                                .build())
                                .collect(Collectors.toList());

                builder.gutTypeStats(gutStats);

                return builder.build();
        }

        // ---------------- ranking formulas ----------------

        private double computeTotalRankingScore(
                        Double avgRating,
                        Long reviewCount,
                        LocalDateTime lastReviewAt) {
                if (reviewCount == null || reviewCount == 0)
                        return 0.0;

                double normalizedAvg = ((avgRating == null ? 0.0 : avgRating) - 1.0) / 4.0;
                double popularity = Math.log(1 + Math.min(reviewCount, 500));

                long days = lastReviewAt == null
                                ? 3650
                                : java.time.temporal.ChronoUnit.DAYS.between(
                                                lastReviewAt,
                                                LocalDateTime.now());

                double recency = Math.exp(-0.01 * days);

                double w1 = 0.6, w2 = 0.3, w3 = 0.1;
                return w1 * normalizedAvg + w2 * popularity + w3 * recency;
        }

        private double computeGutRankingScore(Double avg, Long cnt) {
                if (cnt == null || cnt == 0)
                        return 0.0;

                double normalizedAvg = ((avg == null ? 0.0 : avg) - 1.0) / 4.0;
                double popularity = Math.log(1 + Math.min(cnt, 300));

                double w1 = 0.7, w2 = 0.3;
                return w1 * normalizedAvg + w2 * popularity;
        }
}
