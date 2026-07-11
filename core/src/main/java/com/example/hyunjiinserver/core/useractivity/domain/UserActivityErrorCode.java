package com.example.hyunjiinserver.core.useractivity.domain;

import com.example.hyunjiinserver.core.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserActivityErrorCode implements ErrorCode {

    SAVED_RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "저장한 식당을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    UserActivityErrorCode(HttpStatus status, String message) {
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
