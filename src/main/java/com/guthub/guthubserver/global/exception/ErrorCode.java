package com.guthub.guthubserver.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Auth (401)
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "Access token expired"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "Invalid access token"),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_USER", "Unauthorized user"),

    // Common (400)
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "Invalid parameter included"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Bad request"),
    
    // Not Found (400 or 404 - 프로젝트 정책에 따라 400으로 통일 가능하지만, 리소스 없음은 명확히 구분하는게 좋음)
    // 여기서는 단순화를 위해 400으로 퉁치거나, 404를 유지할 수 있음. 
    // 질문자 의도대로 200, 400, 401 위주라면 404도 400 범주에 넣기도 하지만, 
    // RESTful 관례상 404는 남겨두는 경우가 많음. 일단 요청대로 단순화하여 400 계열로 정의하거나 
    // 최소한의 구분인 404는 남기는 것을 추천. (여기서는 기존 코드 참고하여 404 유지하되 단순화)
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "Resource not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"),

    // Forbidden (403 -> 401 or 400? 보통 권한 없음은 403이지만 복잡도를 낮추려면 400이나 401로 처리하기도 함. 
    // 하지만 401(비로그인)과 403(권한부족)은 엄연히 다름. 
    // 일단 기존 코드의 FORBIDDEN을 유지하되, 복잡하면 400으로 통일 가능. 여기선 표준 준수 추천)
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "Forbidden"),

    // Server Error (500)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Internal server error");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
