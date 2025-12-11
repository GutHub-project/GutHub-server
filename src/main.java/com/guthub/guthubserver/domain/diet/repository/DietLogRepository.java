package com.guthub.guthubserver.domain.diet.repository;

import com.guthub.guthubserver.domain.diet.entity.DietLog;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietLogRepository extends JpaRepository<DietLog, Long> {

    List<DietLog> findAllByUserAndLogDate(UserEntity user, LocalDate logDate);
}
