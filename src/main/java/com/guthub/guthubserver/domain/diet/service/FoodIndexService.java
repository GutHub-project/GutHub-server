package com.guthub.guthubserver.domain.diet.service;

import com.guthub.guthubserver.domain.diet.document.FoodDocument;
import com.guthub.guthubserver.domain.diet.entity.Food;
import com.guthub.guthubserver.domain.diet.repository.FoodRepository;
import com.guthub.guthubserver.domain.diet.repository.FoodSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodIndexService {

    private final FoodRepository foodRepository;
    private final FoodSearchRepository foodSearchRepository;

    /**
     * 모든 음식 데이터를 MySQL에서 Elasticsearch로 인덱싱
     */
    public void indexAllFoods() {
        List<Food> foods = foodRepository.findAll();
        List<FoodDocument> documents = foods.stream()
                .map(this::toDocument)
                .collect(Collectors.toList());

        foodSearchRepository.saveAll(documents);
        log.info("Indexed {} foods to Elasticsearch", documents.size());
    }

    /**
     * 단일 음식 인덱싱 (추가/수정 시 사용)
     */
    public void indexFood(Food food) {
        FoodDocument document = toDocument(food);
        foodSearchRepository.save(document);
        log.debug("Indexed food: {}", food.getName());
    }

    /**
     * 음식 삭제 시 인덱스에서도 삭제
     */
    public void deleteFromIndex(Long foodId) {
        foodSearchRepository.deleteById(foodId);
        log.debug("Deleted food from index: {}", foodId);
    }

    private FoodDocument toDocument(Food food) {
        return FoodDocument.builder()
                .id(food.getId())
                .name(food.getName())
                .calories(food.getCalories())
                .dietaryFiber(food.getDietaryFiber())
                .probiotics(food.getProbiotics())
                .saturatedFat(food.getSaturatedFat())
                .sugar(food.getSugar())
                .refinedCarbs(food.getRefinedCarbs())
                .isFlourBased(food.getIsFlourBased())
                .flour(food.getFlour())
                .build();
    }
}
