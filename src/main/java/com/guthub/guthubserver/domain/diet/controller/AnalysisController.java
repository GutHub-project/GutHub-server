package com.guthub.guthubserver.domain.diet.controller;

import com.guthub.guthubserver.domain.diet.dto.DailySummaryResponseDto;
import com.guthub.guthubserver.domain.diet.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Tag(name = "Analysis", description = "APIs for analyzing diet and gut health")
@SecurityRequirement(name = "bearerAuth")
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/daily-summary")
    @Operation(summary = "Get daily health summary", description = "Provides a summary of gut health for a specific date based on the user's diet logs.")
    public ResponseEntity<DailySummaryResponseDto> getDailySummary(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        DailySummaryResponseDto responseDto = analysisService.getDailySummary(date, userDetails.getUsername());
        return ResponseEntity.ok(responseDto);
    }
}
