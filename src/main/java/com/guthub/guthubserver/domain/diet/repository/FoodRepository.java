package com.guthub.guthubserver.domain.diet.repository;

import com.guthub.guthubserver.domain.diet.entity.Food;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    Optional<Food> findByName(String name);

    List<Food> findByNameContaining(String keyword);
}
