package com.example.hyunjiinserver.user.restaurant.dto;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantCommentResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record RestaurantCommentResponse(
        @Schema(description = "코멘트 ID입니다.", example = "100")
        Long id,

        @Schema(description = "작성자의 현지 맥락입니다. 예: 제주 거주자, 근처 직장인", example = "제주 거주자")
        String authorContext,

        @Schema(description = "현지인 코멘트 본문입니다.", example = "점심시간에는 대기가 있지만 회전이 빨라요.")
        String content,

        @Schema(description = "도움돼요 수입니다.", example = "12")
        int helpfulCount,

        @Schema(description = "코멘트 작성 시각입니다.", example = "2026-07-21T12:30:00+09:00")
        OffsetDateTime createdAt
) {

    public static RestaurantCommentResponse from(RestaurantCommentResult result) {
        return new RestaurantCommentResponse(
                result.id(),
                result.authorContext(),
                result.content(),
                result.helpfulCount(),
                result.createdAt()
        );
    }
}
