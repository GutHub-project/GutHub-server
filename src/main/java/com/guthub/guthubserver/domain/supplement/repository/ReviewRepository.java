package com.guthub.guthubserver.domain.supplement.repository;

import com.guthub.guthubserver.domain.supplement.entity.ReviewEntity;
import com.guthub.guthubserver.domain.supplement.entity.SupplementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    // 특정 건기식의 리뷰 목록 조회 (최신순)
    Page<ReviewEntity> findBySupplementOrderByCreatedAtDesc(SupplementEntity supplement, Pageable pageable);

    // 특정 건기식의 리뷰 개수 조회
    Long countBySupplement(SupplementEntity supplement);

    // 특정 건기식의 평점 평균 조회
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.supplement = :supplement")
    Double getAverageRating(@Param("supplement") SupplementEntity supplement);
}
