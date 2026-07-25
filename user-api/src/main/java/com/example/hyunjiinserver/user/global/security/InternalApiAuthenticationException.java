package com.example.hyunjiinserver.user.global.security;

import org.springframework.http.HttpStatus;

public class InternalApiAuthenticationException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public InternalApiAuthenticationException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
