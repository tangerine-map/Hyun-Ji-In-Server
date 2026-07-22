package com.example.hyunjiinserver.user.restaurant.dto;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantCommentsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record RestaurantCommentsResponse(
        @Schema(description = "식당 상세 화면에 노출할 현지인 코멘트 목록입니다.")
        List<RestaurantCommentResponse> comments
) {

    public static RestaurantCommentsResponse from(RestaurantCommentsResult result) {
        return new RestaurantCommentsResponse(
                result.comments()
                        .stream()
                        .map(RestaurantCommentResponse::from)
                        .toList()
        );
    }
}
