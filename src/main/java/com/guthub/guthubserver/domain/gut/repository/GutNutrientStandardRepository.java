package com.guthub.guthubserver.domain.gut.repository;

import com.guthub.guthubserver.domain.gut.entity.GutNutrientStandard;
import com.guthub.guthubserver.domain.gut.entity.GutType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GutNutrientStandardRepository extends JpaRepository<GutNutrientStandard, Long> {
    List<GutNutrientStandard> findByGutType(GutType gutType);
}
