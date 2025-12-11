package com.guthub.guthubserver.domain.diet.service;

import com.guthub.guthubserver.domain.diet.dto.DietLogRequestDto;
import com.guthub.guthubserver.domain.diet.dto.DietLogResponseDto;
import com.guthub.guthubserver.domain.diet.entity.DietLog;
import com.guthub.guthubserver.domain.diet.entity.Food;
import com.guthub.guthubserver.domain.diet.repository.DietLogRepository;
import com.guthub.guthubserver.domain.diet.repository.FoodRepository;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DietLogService {

    private final DietLogRepository dietLogRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    @Transactional
    public DietLogResponseDto createDietLog(DietLogRequestDto requestDto, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Food food = foodRepository.findById(requestDto.getFoodId())
                .orElseThrow(() -> new IllegalArgumentException("Food not found"));

        DietLog dietLog = DietLog.builder()
                .user(user)
                .food(food)
                .logDate(requestDto.getLogDate())
                .logTime(requestDto.getLogTime())
                .amount(requestDto.getAmount())
                .mealType(requestDto.getMealType())
                .build();

        DietLog savedDietLog = dietLogRepository.save(dietLog);
        return new DietLogResponseDto(savedDietLog);
    }

    public List<DietLogResponseDto> getDietLogsByDate(LocalDate date, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<DietLog> dietLogs = dietLogRepository.findAllByUserAndLogDate(user, date);

        return dietLogs.stream()
                .map(DietLogResponseDto::new)
                .collect(Collectors.toList());
    }
}
