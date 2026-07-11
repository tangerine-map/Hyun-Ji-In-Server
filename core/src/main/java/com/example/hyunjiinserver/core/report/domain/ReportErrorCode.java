package com.example.hyunjiinserver.core.report.domain;

import com.example.hyunjiinserver.core.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ReportErrorCode implements ErrorCode {

    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "신고 내역을 찾을 수 없습니다."),
    INVALID_REPORT_TYPE(HttpStatus.BAD_REQUEST, "신고 유형이 올바르지 않습니다.");

    private final HttpStatus status;
    private final String message;

    ReportErrorCode(HttpStatus status, String message) {
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
