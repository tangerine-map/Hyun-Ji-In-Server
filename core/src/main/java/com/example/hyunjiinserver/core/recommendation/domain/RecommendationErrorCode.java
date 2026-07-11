package com.example.hyunjiinserver.core.recommendation.domain;

import com.example.hyunjiinserver.core.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum RecommendationErrorCode implements ErrorCode {

    RECOMMENDATION_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "추천 세션을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    RecommendationErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String code() {
        return name();
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}
