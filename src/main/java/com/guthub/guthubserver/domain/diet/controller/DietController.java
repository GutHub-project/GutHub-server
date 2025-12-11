package com.guthub.guthubserver.domain.diet.controller;

import com.guthub.guthubserver.domain.diet.dto.DietLogRequestDto;
import com.guthub.guthubserver.domain.diet.dto.DietLogResponseDto;
import com.guthub.guthubserver.domain.diet.service.DietLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diets")
@RequiredArgsConstructor
@Tag(name = "Diet", description = "Diet logging and retrieval APIs")
@SecurityRequirement(name = "bearerAuth")
public class DietController {

    private final DietLogService dietLogService;

    @PostMapping
    @Operation(summary = "Create a diet log", description = "Records a new meal for the authenticated user.")
    public ResponseEntity<DietLogResponseDto> createDietLog(
            @Valid @RequestBody DietLogRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        DietLogResponseDto responseDto = dietLogService.createDietLog(requestDto, userDetails.getUsername());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    @Operation(summary = "Get diet logs by date", description = "Retrieves all diet logs for a specific date for the authenticated user.")
    public ResponseEntity<List<DietLogResponseDto>> getDietLogsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<DietLogResponseDto> responseDtos = dietLogService.getDietLogsByDate(date, userDetails.getUsername());
        return ResponseEntity.ok(responseDtos);
    }
}
