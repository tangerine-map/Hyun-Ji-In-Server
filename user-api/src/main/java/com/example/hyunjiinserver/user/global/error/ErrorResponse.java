package com.example.hyunjiinserver.user.global.error;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "에러 코드입니다.", example = "RESTAURANT_NOT_FOUND")
        String code,

        @Schema(description = "사용자 또는 클라이언트 개발자에게 전달할 에러 메시지입니다.", example = "식당을 찾을 수 없습니다.")
        String message
) {
}
