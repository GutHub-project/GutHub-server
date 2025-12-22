package com.guthub.guthubserver.domain.gut.service;

import com.guthub.guthubserver.domain.gut.dto.GutHealthScoreResponseDto;
import com.guthub.guthubserver.domain.gut.dto.MonthlyGutHealthResponseDto;
import com.guthub.guthubserver.domain.gut.entity.DailyGutHealthScore;
import com.guthub.guthubserver.domain.gut.repository.DailyGutHealthScoreRepository;
import com.guthub.guthubserver.domain.user.entity.UserEntity;
import com.guthub.guthubserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GutHealthScoreService {

    private final DailyGutHealthScoreRepository dailyGutHealthScoreRepository;
    private final UserRepository userRepository;

    public GutHealthScoreResponseDto getGutHealthScore(LocalDate date) {
        // 인증된 사용자 정보 추출
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 미래 날짜 검증
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("미래의 날짜는 조회할 수 없습니다.");
        }

        // 데이터 조회
        DailyGutHealthScore score = dailyGutHealthScoreRepository
                .findByUser_IdAndRecordDate(user.getId(), date)
                .orElseThrow(() -> new NoSuchElementException("해당 날짜의 데이터가 없습니다."));

        // DTO 변환 및 반환
        return GutHealthScoreResponseDto.builder()
                .status(score.getOverallStatus().name())
                .badCount(score.getBadCount())
                .violationReason(score.getViolationReason())
                .updatedAt(score.getUpdatedAt())
                .build();
    }

    public MonthlyGutHealthResponseDto getMonthlyGutHealthScore(String month) {
        // 인증된 사용자 정보 추출
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // month 파라미터 파싱 (yyyy-MM 형식)
        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("날짜 또는 달을 입력하지 않았습니다.");
        }

        // 미래 달 검증
        if (yearMonth.isAfter(YearMonth.now())) {
            throw new IllegalArgumentException("달이 유효하지 않습니다.");
        }

        // 해당 월의 시작일과 종료일 계산
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 데이터 조회
        List<DailyGutHealthScore> scores = dailyGutHealthScoreRepository
                .findByUser_IdAndRecordDateBetweenOrderByRecordDateAsc(user.getId(), startDate, endDate);

        // DTO 변환
        List<MonthlyGutHealthResponseDto.DailyStatus> statusList = scores.stream()
                .map(score -> MonthlyGutHealthResponseDto.DailyStatus.builder()
                        .date(score.getRecordDate().toString())
                        .status(score.getOverallStatus().name())
                        .badCount(score.getBadCount())
                        .violationReason(score.getViolationReason())
                        .updatedAt(score.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return MonthlyGutHealthResponseDto.builder()
                .statusList(statusList)
                .build();
    }
}
