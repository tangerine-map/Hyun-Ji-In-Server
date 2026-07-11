package com.example.hyunjiinserver.user.global.error;

import java.util.List;

public record ValidationErrorResponse(
        String code,
        String message,
        List<FieldError> fields
) {

    public record FieldError(
            String field,
            String message
    ) {
    }
}
