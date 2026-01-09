package com.guthub.guthubserver.domain.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 통합검색 API 응답 DTO
 */
@Getter
@Builder
public class ProductSearchResponseDto {

    private String query;
    private List<SupplementSearchItemDto> supplementList;
    private String nextCursor;
    private boolean hasNext;
}
