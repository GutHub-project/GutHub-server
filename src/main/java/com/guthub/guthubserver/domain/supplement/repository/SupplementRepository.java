package com.guthub.guthubserver.domain.supplement.repository;

import com.guthub.guthubserver.domain.supplement.entity.SupplementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplementRepository extends JpaRepository<SupplementEntity, Long> {
}
