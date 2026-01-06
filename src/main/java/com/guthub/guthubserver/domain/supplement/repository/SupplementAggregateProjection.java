package com.guthub.guthubserver.domain.supplement.repository;

import java.time.LocalDateTime;

public interface SupplementAggregateProjection {
    Long getSupplementId();
    Long getReviewCount();
    Double getAvgRating();
    LocalDateTime getLastReviewAt();
}

