package com.guthub.guthubserver.config;

import com.guthub.guthubserver.domain.jwt.repository.RefreshRepository;
import com.guthub.guthubserver.domain.supplement.service.SupplementStatsAggregator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduleConfig {

    private final RefreshRepository refreshRepository;

    private final SupplementStatsAggregator aggregator;

    // Refresh 토큰 저장소 8일 지난 토큰 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void refreshEntityTtlSchedule() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(8);
        refreshRepository.deleteByCreatedAtBefore(cutoff);
    }

    // 기본: 매일 02:00 실행, application.yml에서 커스터마이징 가능
    @Scheduled(cron = "0 0 2 * * *")
    public void dailyAggregate() {
        aggregator.aggregateAndIndexAll();
    }
}

