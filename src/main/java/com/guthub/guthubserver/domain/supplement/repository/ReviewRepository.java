package com.guthub.guthubserver.domain.supplement.repository;

import com.guthub.guthubserver.domain.supplement.entity.ReviewEntity;
import com.guthub.guthubserver.domain.supplement.entity.SupplementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    // 특정 건기식의 리뷰 목록 조회 (최신순)
    Page<ReviewEntity> findBySupplementOrderByCreatedAtDesc(SupplementEntity supplement, Pageable pageable);

    // 특정 건기식의 리뷰 개수 조회
    Long countBySupplement(SupplementEntity supplement);

    // 특정 건기식의 평점 평균 조회
    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.supplement = :supplement")
    Double getAverageRating(@Param("supplement") SupplementEntity supplement);

    // --- Batch aggregation queries for scheduler ---
    // Aggregate per supplement (review_count, avg_rating, last_review_at)
    @Query(value = "SELECT r.supplement_id as supplementId, COUNT(*) as reviewCount, AVG(r.rating) as avgRating, MAX(r.created_at) as lastReviewAt FROM reviews r WHERE r.deleted = false GROUP BY r.supplement_id", nativeQuery = true)
    List<SupplementAggregateProjection> aggregatePerSupplementAll();

    // Aggregate per supplement + gut type
    @Query(value = "SELECT r.supplement_id as supplementId, r.gut_type_code as gutTypeCode, COUNT(*) as reviewCount, AVG(r.rating) as avgRating FROM reviews r WHERE r.deleted = false GROUP BY r.supplement_id, r.gut_type_code", nativeQuery = true)
    List<SupplementGutAggregateProjection> aggregatePerSupplementByGutAll();
}
