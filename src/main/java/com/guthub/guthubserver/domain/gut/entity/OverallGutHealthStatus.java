package com.guthub.guthubserver.domain.gut.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OverallGutHealthStatus {
    GOOD("좋음"),
    NORMAL("보통"),
    BAD("나쁨");

    private final String displayName;

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}
