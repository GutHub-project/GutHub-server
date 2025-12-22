package com.guthub.guthubserver.api;

import com.guthub.guthubserver.domain.gut.dto.GutHealthScoreResponseDto;
import com.guthub.guthubserver.domain.gut.dto.MonthlyGutHealthResponseDto;
import com.guthub.guthubserver.domain.gut.service.GutHealthScoreService;
import com.guthub.guthubserver.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me")
public class GutHealthController {

    private final GutHealthScoreService gutHealthScoreService;

    @GetMapping("/gut-health")
    public ResponseEntity<ApiResponse<GutHealthScoreResponseDto>> getGutHealthScore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        GutHealthScoreResponseDto result = gutHealthScoreService.getGutHealthScore(date);
        return ResponseEntity.ok(
                ApiResponse.of("SUCCESS", "요청이 성공하였습니다.", result));
    }

    @GetMapping("/gut-health/monthly")
    public ResponseEntity<ApiResponse<MonthlyGutHealthResponseDto>> getMonthlyGutHealthScore(
            @RequestParam String month) {
        MonthlyGutHealthResponseDto result = gutHealthScoreService.getMonthlyGutHealthScore(month);
        return ResponseEntity.ok(
                ApiResponse.of("SUCCESS", "요청이 성공하였습니다.", result));
    }
}
