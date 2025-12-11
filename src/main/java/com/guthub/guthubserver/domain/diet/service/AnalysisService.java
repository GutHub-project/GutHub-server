package com.guthub.guthubserver.domain.diet.service;

import com.guthub.guthubserver.domain.diet.dto.DailySummaryResponseDto;
import com.guthub.guthubserver.domain.diet.entity.DietLog;
import com.guthub.guthubserver.domain.diet.repository.DietLogRepository;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private final DietLogRepository dietLogRepository;
    private final UserRepository userRepository;

    public DailySummaryResponseDto getDailySummary(LocalDate date, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<DietLog> dietLogs = dietLogRepository.findAllByUserAndLogDate(user, date);

        Map<String, Float> totalNutrients = calculateTotalNutrients(dietLogs);
        String gutHealthStatus = evaluateGutHealth(totalNutrients, user.getGutType());
        String violationReason = generateViolationReason(totalNutrients, user.getGutType());

        return DailySummaryResponseDto.builder()
                .date(date)
                .gutHealthStatus(gutHealthStatus)
                .violationReason(violationReason)
                .totalNutrients(totalNutrients)
                .build();
    }

    private Map<String, Float> calculateTotalNutrients(List<DietLog> dietLogs) {
        Map<String, Float> totalNutrients = new HashMap<>();
        // Nutrient calculation logic here...
        // For example:
        float totalCalories = 0f;
        float totalDietaryFiber = 0f;
        for (DietLog log : dietLogs) {
            totalCalories += log.getFood().getCalories() * log.getAmount();
            totalDietaryFiber += log.getFood().getDietaryFiber() * log.getAmount();
        }
        totalNutrients.put("calories", totalCalories);
        totalNutrients.put("dietaryFiber", totalDietaryFiber);
        return totalNutrients;
    }

    private String evaluateGutHealth(Map<String, Float> totalNutrients, String gutType) {
        // Gut health evaluation logic based on nutrients and gut type...
        // For example, simple logic based on dietary fiber:
        if (totalNutrients.getOrDefault("dietaryFiber", 0f) > 25) {
            return "GOOD";
        } else if (totalNutrients.getOrDefault("dietaryFiber", 0f) > 15) {
            return "NORMAL";
        } else {
            return "BAD";
        }
    }

    private String generateViolationReason(Map<String, Float> totalNutrients, String gutType) {
        // Violation reason generation logic...
        if ("BAD".equals(evaluateGutHealth(totalNutrients, gutType))) {
            return "Not enough dietary fiber.";
        }
        return "No significant violations.";
    }
}
