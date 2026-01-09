package com.guthub.guthubserver.api;

import com.guthub.guthubserver.domain.search.dto.ProductSearchResponseDto;
import com.guthub.guthubserver.domain.search.service.ProductSearchService;
import com.guthub.guthubserver.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "통합검색 API")
public class SearchController {

    private final ProductSearchService productSearchService;

    @GetMapping("/products")
    @Operation(summary = "제품 검색", description = "키워드로 건기식 제품을 검색합니다. 커서 기반 페이지네이션을 지원합니다.")
    public ResponseEntity<ApiResponse<ProductSearchResponseDto>> searchProducts(
            @Parameter(description = "검색 키워드", required = true, example = "프로바이오틱스") @RequestParam("q") String query,
            @Parameter(description = "페이지네이션 커서 (첫 페이지는 생략)", required = false) @RequestParam(value = "cursor", required = false) String cursor) {
        // 쿼리 파라미터 검증
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.of("INVALID_PARAMETER", "쿼리를 입력하지 않았습니다."));
        }

        try {
            ProductSearchResponseDto response = productSearchService.searchProducts(query.trim(), cursor);
            return ResponseEntity.ok(ApiResponse.of("SUCCESS", "요청이 성공하였습니다.", response));
        } catch (IllegalArgumentException e) {
            // 잘못된 커서
            return ResponseEntity.status(org.springframework.http.HttpStatusCode.valueOf(422))
                    .body(ApiResponse.of("UNPROCESSABLE_ENTITY", e.getMessage()));
        }
    }
}
