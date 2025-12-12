package com.guthub.guthubserver.domain.diet.repository;

import com.guthub.guthubserver.domain.diet.entity.DietLog;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DietLogRepository extends JpaRepository<DietLog, Long> {

    // logDate 필드를 직접 비교하여 조회
    @Query("SELECT dl FROM DietLog dl WHERE dl.user = :user AND dl.logDate = :logDate")
    List<DietLog> findAllByUserAndLogDate(@Param("user") UserEntity user, @Param("logDate") LocalDate logDate);
}
