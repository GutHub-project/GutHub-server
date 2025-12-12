package com.guthub.guthubserver.domain.diet.service;

import com.guthub.guthubserver.domain.diet.dto.DailyDietSummaryResponseDto;
import com.guthub.guthubserver.domain.diet.dto.DietLogRequestDto;
import com.guthub.guthubserver.domain.diet.dto.DietLogResponseDto;
import com.guthub.guthubserver.domain.diet.dto.DietLogUpdateRequestDto;
import com.guthub.guthubserver.domain.diet.dto.FoodSearchResponseDto;
import com.guthub.guthubserver.domain.diet.dto.GutHealthStreakResponseDto;
import com.guthub.guthubserver.domain.diet.dto.NutrientStatus;
import com.guthub.guthubserver.domain.diet.entity.DietLog;
import com.guthub.guthubserver.domain.diet.entity.Food;
import com.guthub.guthubserver.domain.diet.entity.MealType;
import com.guthub.guthubserver.domain.diet.repository.DietLogRepository;
import com.guthub.guthubserver.domain.diet.repository.FoodRepository;
import com.guthub.guthubserver.domain.gut.entity.DailyGutHealthScore;
import com.guthub.guthubserver.domain.gut.entity.GutNutrientStandard;
import com.guthub.guthubserver.domain.gut.entity.GutType;
import com.guthub.guthubserver.domain.gut.entity.OverallGutHealthStatus;
import com.guthub.guthubserver.domain.gut.repository.DailyGutHealthScoreRepository;
import com.guthub.guthubserver.domain.gut.repository.GutNutrientStandardRepository;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder; // SecurityContextHolder import 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DietLogService {

    private final DietLogRepository dietLogRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final GutNutrientStandardRepository gutNutrientStandardRepository;
    private final DailyGutHealthScoreRepository dailyGutHealthScoreRepository;

    @Transactional
    public List<DietLogResponseDto> createDietLog(DietLogRequestDto requestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 서비스에서 사용자 이름 가져오기
        UserEntity user = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<DietLog> savedDietLogs = requestDto.getItems().stream()
                .map(itemDto -> {
                    Food food = foodRepository.findByName(itemDto.getFoodName())
                            .orElseThrow(() -> new IllegalArgumentException("Food not found: " + itemDto.getFoodName()));

                    DietLog dietLog = DietLog.builder()
                            .user(user)
                            .food(food)
                            .logDate(requestDto.getLogDate())
                            .amount(itemDto.getAmount())
                            .mealType(itemDto.getMealType())
                            .build();
                    return dietLogRepository.save(dietLog);
                })
                .collect(Collectors.toList());

        // 식단 기록 후 일일 건강 점수 업데이트
        updateDailyGutHealthScore(user, requestDto.getLogDate());

        return savedDietLogs.stream()
                .map(DietLogResponseDto::new)
                .collect(Collectors.toList());
    }

    public DailyDietSummaryResponseDto getDietLogsByDate(LocalDate date) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 서비스에서 사용자 이름 가져오기
        UserEntity user = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<DietLog> dietLogs = dietLogRepository.findAllByUserAndLogDate(user, date);

        // 식단 항목을 mealType별로 그룹화
        Map<MealType, List<DietLogResponseDto>> categorizedDietLogs = dietLogs.stream()
                .map(DietLogResponseDto::new)
                .collect(Collectors.groupingBy(DietLogResponseDto::getMealType));

        DailyDietSummaryResponseDto.TotalNutrientInfo totalNutrientInfo = calculateTotalNutrients(dietLogs);
        DailyDietSummaryResponseDto.GutHealthAnalysis gutHealthAnalysis = analyzeGutHealth(user.getGutType(), totalNutrientInfo);

        return DailyDietSummaryResponseDto.builder()
                .date(date)
                .categorizedDietLogs(categorizedDietLogs)
                .totalNutrientInfo(totalNutrientInfo)
                .gutHealthAnalysis(gutHealthAnalysis)
                .build();
    }

    // --- ID로 식단 기록 조회 메소드 추가 ---
    public DietLogResponseDto getDietLogById(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 서비스에서 사용자 이름 가져오기
        UserEntity user = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        DietLog dietLog = dietLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Diet log not found with ID: " + id));

        if (!dietLog.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to view this diet log.");
        }

        return new DietLogResponseDto(dietLog);
    }

    @Transactional
    public DietLogResponseDto updateDietLog(Long id, DietLogUpdateRequestDto requestDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 서비스에서 사용자 이름 가져오기
        UserEntity user = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        DietLog dietLog = dietLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Diet log not found with ID: " + id));

        if (!dietLog.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to update this diet log.");
        }

        Food food = foodRepository.findByName(requestDto.getFoodName())
                .orElseThrow(() -> new IllegalArgumentException("Food not found: " + requestDto.getFoodName()));

        dietLog.updateFood(food);
        dietLog.updateAmount(requestDto.getAmount());
        dietLog.updateMealType(requestDto.getMealType());

        DietLog updatedDietLog = dietLogRepository.save(dietLog);

        // 식단 수정 후 일일 건강 점수 업데이트
        updateDailyGutHealthScore(user, updatedDietLog.getLogDate());
        return new DietLogResponseDto(updatedDietLog);
    }

    @Transactional
    public void deleteDietLog(Long dietLogId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 서비스에서 사용자 이름 가져오기
        UserEntity user = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        DietLog dietLog = dietLogRepository.findById(dietLogId)
                .orElseThrow(() -> new IllegalArgumentException("Diet log not found with ID: " + dietLogId));

        if (!dietLog.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to delete this diet log.");
        }

        LocalDate logDate = dietLog.getLogDate();
        dietLogRepository.delete(dietLog);

        // 식단 삭제 후 일일 건강 점수 업데이트
        updateDailyGutHealthScore(user, logDate);
    }

    public List<FoodSearchResponseDto> searchFoods(String keyword) {
        return foodRepository.findByNameContaining(keyword).stream()
                .map(FoodSearchResponseDto::new)
                .collect(Collectors.toList());
    }

    public GutHealthStreakResponseDto getCurrentGutHealthStreak() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName(); // 서비스에서 사용자 이름 가져오기
        UserEntity user = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Map<String, Object> streakResult = dailyGutHealthScoreRepository.getGutHealthStreakNative(user.getId());

        int streakCount = 0;
        LocalDate lastRecordDate = null;

        if (streakResult != null) {
            Object countObj = streakResult.get("streakCount");
            if (countObj instanceof BigInteger) {
                streakCount = ((BigInteger) countObj).intValue();
            } else if (countObj instanceof Long) {
                streakCount = ((Long) countObj).intValue();
            } else if (countObj instanceof Integer) {
                streakCount = (Integer) countObj;
            }

            Object dateObj = streakResult.get("lastRecordDate");
            if (dateObj instanceof LocalDate) {
                lastRecordDate = (LocalDate) dateObj;
            } else if (dateObj instanceof java.sql.Date) { // JDBC Date 타입 처리
                lastRecordDate = ((java.sql.Date) dateObj).toLocalDate();
            } else if (dateObj instanceof String) { // String으로 반환될 경우 파싱
                lastRecordDate = LocalDate.parse((String) dateObj);
            }
        }

        return GutHealthStreakResponseDto.builder()
                .streakCount(streakCount)
                .lastRecordDate(lastRecordDate != null ? lastRecordDate.toString() : null)
                .build();
    }

    @Transactional
    public void updateDailyGutHealthScore(UserEntity user, LocalDate date) {
        List<DietLog> dietLogs = dietLogRepository.findAllByUserAndLogDate(user, date);
        DailyDietSummaryResponseDto.TotalNutrientInfo totalNutrientInfo = calculateTotalNutrients(dietLogs);
        DailyDietSummaryResponseDto.GutHealthAnalysis gutHealthAnalysis = analyzeGutHealth(user.getGutType(), totalNutrientInfo);

        dailyGutHealthScoreRepository.findByUserAndRecordDate(user, date)
                .ifPresentOrElse(
                        score -> score.updateOverallStatus(gutHealthAnalysis.getOverallStatus()),
                        () -> {
                            DailyGutHealthScore newScore = DailyGutHealthScore.builder()
                                    .user(user)
                                    .recordDate(date)
                                    .overallStatus(gutHealthAnalysis.getOverallStatus())
                                    .build();
                            dailyGutHealthScoreRepository.save(newScore);
                        });
    }


    private DailyDietSummaryResponseDto.TotalNutrientInfo calculateTotalNutrients(List<DietLog> dietLogs) {
        float totalCalories = 0;
        float totalDietaryFiber = 0;
        float totalProbiotics = 0;
        float totalSaturatedFat = 0;
        float totalSugar = 0;
        float totalRefinedCarbs = 0;
        float totalFlour = 0;

        for (DietLog dietLog : dietLogs) {
            Food food = dietLog.getFood();
            float amount = dietLog.getAmount() != null ? dietLog.getAmount() : 1.0f;

            totalCalories += food.getCalories() * amount;
            totalDietaryFiber += food.getDietaryFiber() * amount;
            totalProbiotics += food.getProbiotics() * amount;
            totalSaturatedFat += food.getSaturatedFat() * amount;
            totalSugar += food.getSugar() * amount;
            totalRefinedCarbs += food.getRefinedCarbs() * amount;
            totalFlour += food.getFlour() * amount;
        }

        return DailyDietSummaryResponseDto.TotalNutrientInfo.builder()
                .totalCalories(totalCalories)
                .totalDietaryFiber(totalDietaryFiber)
                .totalProbiotics(totalProbiotics)
                .totalSaturatedFat(totalSaturatedFat)
                .totalSugar(totalSugar)
                .totalRefinedCarbs(totalRefinedCarbs)
                .totalFlour(totalFlour)
                .build();
    }

    private DailyDietSummaryResponseDto.GutHealthAnalysis analyzeGutHealth(GutType userGutType, DailyDietSummaryResponseDto.TotalNutrientInfo totalNutrientInfo) {
        List<GutNutrientStandard> standards = gutNutrientStandardRepository.findByGutType(userGutType);
        List<DailyDietSummaryResponseDto.GutHealthAnalysis.NutrientComparison> comparisons = new ArrayList<>();
        int exceededCount = 0;

        Map<String, Float> dailyIntakes = new HashMap<>();
        dailyIntakes.put("dietaryFiber", totalNutrientInfo.getTotalDietaryFiber());
        dailyIntakes.put("probiotics", totalNutrientInfo.getTotalProbiotics());
        dailyIntakes.put("saturatedFat", totalNutrientInfo.getTotalSaturatedFat());
        dailyIntakes.put("sugar", totalNutrientInfo.getTotalSugar());
        dailyIntakes.put("refinedCarbs", totalNutrientInfo.getTotalRefinedCarbs());

        for (GutNutrientStandard standard : standards) {
            String nutrientName = standard.getNutrient_name();
            Float minLimit = standard.getMin_limit();
            Float maxLimit = standard.getMax_limit();
            Float dailyIntake = dailyIntakes.getOrDefault(nutrientName, 0.0f);

            NutrientStatus status;
            boolean isExceeded = false;
            boolean isBelowMin = false;

            if (maxLimit != null && dailyIntake > maxLimit) {
                status = NutrientStatus.EXCEEDED;
                isExceeded = true;
                exceededCount++;
            } else if (minLimit != null && dailyIntake < minLimit) {
                status = NutrientStatus.BELOW_MIN;
                isBelowMin = true;
                exceededCount++;
            } else {
                status = NutrientStatus.OPTIMAL;
            }

            comparisons.add(DailyDietSummaryResponseDto.GutHealthAnalysis.NutrientComparison.builder()
                    .nutrientName(nutrientName)
                    .dailyIntake(dailyIntake)
                    .minLimit(minLimit)
                    .maxLimit(maxLimit)
                    .status(status)
                    .isExceeded(isExceeded)
                    .isBelowMin(isBelowMin)
                    .build());
        }

        OverallGutHealthStatus overallStatus;
        if (exceededCount >= 4) {
            overallStatus = OverallGutHealthStatus.BAD;
        } else if (exceededCount >= 1) {
            overallStatus = OverallGutHealthStatus.NORMAL;
        } else {
            overallStatus = OverallGutHealthStatus.GOOD;
        }

        return DailyDietSummaryResponseDto.GutHealthAnalysis.builder()
                .overallStatus(overallStatus)
                .comparisons(comparisons)
                .build();
    }
}
