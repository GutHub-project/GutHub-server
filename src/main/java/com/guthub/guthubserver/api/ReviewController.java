package com.guthub.guthubserver.api;

import com.guthub.guthubserver.domain.supplement.dto.ReviewRequestDto;
import com.guthub.guthubserver.domain.supplement.dto.ReviewResponseDto;
import com.guthub.guthubserver.domain.supplement.service.ReviewService;
import com.guthub.guthubserver.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review API", description = "건기식 리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "리뷰 작성", description = "특정 건기식에 대한 리뷰를 작성합니다.")
    public ApiResponse<ReviewResponseDto> createReview(
            @Valid @RequestBody ReviewRequestDto requestDto) {
        
        // Controller는 단순히 요청을 Service로 전달만 함 (User 정보는 Service에서 처리)
        ReviewResponseDto response = reviewService.createReview(requestDto);
        return ApiResponse.of("SUCCESS", "Review created successfully", response);
    }

    @GetMapping("/{supplementId}")
    @Operation(summary = "리뷰 조회", description = "특정 건기식의 리뷰 목록을 조회합니다.")
    public ApiResponse<Page<ReviewResponseDto>> getReviews(
            @PathVariable Long supplementId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<ReviewResponseDto> response = reviewService.getReviews(supplementId, pageable);
        return ApiResponse.of("SUCCESS", "Reviews retrieved successfully", response);
    }
}
