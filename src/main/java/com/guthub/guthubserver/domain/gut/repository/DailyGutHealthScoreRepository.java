package com.guthub.guthubserver.domain.gut.repository;

import com.guthub.guthubserver.domain.gut.entity.DailyGutHealthScore;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DailyGutHealthScoreRepository extends JpaRepository<DailyGutHealthScore, Long> {
    Optional<DailyGutHealthScore> findByUserAndRecordDate(UserEntity user, LocalDate recordDate);

    List<DailyGutHealthScore> findByUserOrderByRecordDateDesc(UserEntity user);

    @Query(value = """
        SELECT
            COUNT(dghs.id) AS streakCount,
            MAX(dghs.record_date) AS lastRecordDate
        FROM
            daily_gut_health_scores dghs
        WHERE
            dghs.user_id = :userId
            AND dghs.overall_status = 'GOOD'
            AND dghs.record_date <= CURRENT_DATE()
            AND dghs.record_date > COALESCE(
                (SELECT MAX(dghs2.record_date)
                 FROM daily_gut_health_scores dghs2
                 WHERE dghs2.user_id = :userId
                   AND dghs2.overall_status != 'GOOD'
                   AND dghs2.record_date < CURRENT_DATE()),
                DATE '1900-01-01'
            )
        """, nativeQuery = true)
    Map<String, Object> getGutHealthStreakNative(@Param("userId") Long userId);
}
