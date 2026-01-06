package com.guthub.guthubserver.domain.supplement.repository;

import com.guthub.guthubserver.domain.supplement.entity.SupplementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SupplementRepository extends JpaRepository<SupplementEntity, Long> {

    @Query("SELECT s.id FROM SupplementEntity s")
    Page<Long> findAllIds(Pageable pageable);
}
