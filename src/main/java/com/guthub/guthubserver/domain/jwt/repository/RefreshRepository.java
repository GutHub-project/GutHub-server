package com.guthub.guthubserver.domain.jwt.repository;

import com.guthub.guthubserver.domain.jwt.entity.RefreshEntity;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByRefresh(String refreshToken);

    void deleteByRefresh(String refresh);

    void deleteByUsername(String username);

    // 특정일 지난 refresh 토큰 삭제
    @Transactional
    void deleteByCreatedDateBefore(LocalDateTime createdDate);
}
