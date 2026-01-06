package com.guthub.guthubserver.domain.supplement.repository;

public interface SupplementGutAggregateProjection {
    Long getSupplementId();
    String getGutTypeCode();
    Long getReviewCount();
    Double getAvgRating();
}

