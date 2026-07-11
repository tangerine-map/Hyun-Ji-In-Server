package com.example.hyunjiinserver.core.search.domain;

import com.example.hyunjiinserver.core.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum SearchErrorCode implements ErrorCode {

    INVALID_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "검색어가 올바르지 않습니다.");

    private final HttpStatus status;
    private final String message;

    SearchErrorCode(HttpStatus status, String message) {
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
