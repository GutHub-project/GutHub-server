package com.guthub.guthubserver.api;


import com.guthub.guthubserver.domain.diet.dto.DailyDietSummaryResponseDto;
import com.guthub.guthubserver.domain.diet.dto.DietLogRequestDto;
import com.guthub.guthubserver.domain.diet.dto.DietLogResponseDto;
import com.guthub.guthubserver.domain.diet.dto.DietLogUpdateRequestDto;
import com.guthub.guthubserver.domain.diet.dto.FoodSearchResponseDto;
import com.guthub.guthubserver.domain.diet.dto.GutHealthStreakResponseDto;
import com.guthub.guthubserver.domain.diet.service.DietLogService;
import com.guthub.guthubserver.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diets")
@RequiredArgsConstructor
@Tag(name = "Diet", description = "Diet logging and retrieval APIs")
@SecurityRequirement(name = "bearerAuth")
public class DietController {

    private final DietLogService dietLogService;

    @PostMapping
    @Operation(summary = "Create multiple diet logs", description = "Records multiple meals for the authenticated user on a specific date.")
    public ResponseEntity<ApiResponse<List<DietLogResponseDto>>> createDietLog(
            @Valid @RequestBody DietLogRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<DietLogResponseDto> responseDtos = dietLogService.createDietLog(requestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("SUCCESS", "식단 기록 성공", responseDtos));
    }

    @GetMapping
    @Operation(summary = "Get diet logs by date", description = "Retrieves all diet logs for a specific date for the authenticated user.")
    public ResponseEntity<ApiResponse<DailyDietSummaryResponseDto>> getDietLogsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        DailyDietSummaryResponseDto responseDto = dietLogService.getDietLogsByDate(date, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "식단 조회 성공", responseDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a diet log by ID", description = "Retrieves a single diet log entry by its ID.")
    public ResponseEntity<ApiResponse<DietLogResponseDto>> getDietLogById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        DietLogResponseDto responseDto = dietLogService.getDietLogById(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "식단 상세 조회 성공", responseDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a diet log", description = "Updates an existing diet log entry for the authenticated user.")
    public ResponseEntity<ApiResponse<DietLogResponseDto>> updateDietLog(
            @PathVariable Long id,
            @Valid @RequestBody DietLogUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        DietLogResponseDto responseDto = dietLogService.updateDietLog(id, requestDto, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "식단 수정 성공", responseDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a diet log", description = "Deletes a specific diet log entry for the authenticated user.")
    public ResponseEntity<ApiResponse<Void>> deleteDietLog(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        dietLogService.deleteDietLog(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "식단 삭제 성공"));
    }

    @GetMapping("/search-foods")
    @Operation(summary = "Search foods by keyword", description = "Searches for foods whose names contain the given keyword.")
    public ResponseEntity<ApiResponse<List<FoodSearchResponseDto>>> searchFoods(
            @RequestParam("keyword") String keyword) {
        List<FoodSearchResponseDto> response = dietLogService.searchFoods(keyword);
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "음식 검색 성공", response));
    }

    @GetMapping("/streak")
    @Operation(summary = "Get current gut health streak", description = "Retrieves the current consecutive days of good gut health.")
    public ResponseEntity<ApiResponse<GutHealthStreakResponseDto>> getGutHealthStreak(
            @AuthenticationPrincipal UserDetails userDetails) {
        GutHealthStreakResponseDto response = dietLogService.getCurrentGutHealthStreak(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of("SUCCESS", "장 건강 연속 유지일 조회 성공", response));
    }
}
