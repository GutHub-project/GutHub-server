package com.guthub.guthubserver.domain.gut.repository;

import com.guthub.guthubserver.domain.gut.entity.GutType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GutTypeRepository extends JpaRepository<GutType, Long> {
    Optional<GutType> findByName(String name);
}
