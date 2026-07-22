package com.example.hyunjiinserver.user.global.error.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ValidationErrorResponse(
        @Schema(description = "검증 실패 에러 코드입니다.", example = "VALIDATION_ERROR")
        String code,

        @Schema(description = "검증 실패 공통 메시지입니다.", example = "요청 값이 올바르지 않습니다.")
        String message,

        @Schema(description = "필드별 검증 실패 상세 목록입니다.")
        List<FieldError> fields
) {

    public record FieldError(
            @Schema(description = "검증에 실패한 요청 필드명입니다.", example = "limit")
            String field,

            @Schema(description = "필드 검증 실패 사유입니다.", example = "조회 개수는 100 이하여야 합니다.")
            String message
    ) {
    }
}
