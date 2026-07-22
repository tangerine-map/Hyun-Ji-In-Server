package com.example.hyunjiinserver.user.restaurant.dto;

import com.example.hyunjiinserver.core.restaurant.application.GetRestaurantCommentsQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RestaurantCommentSearchRequest(
        @Schema(
                description = "코멘트 정렬 기준입니다. 생략하면 서버 기본 정렬이 적용됩니다. 예: 최신순 latest, 도움순 helpful",
                example = "latest"
        )
        String sort,

        @Schema(description = "최대 조회 개수입니다. 생략하면 서버 기본값이 적용되며, 최대 100개까지 조회합니다.", example = "20")
        @Min(value = 1, message = "조회 개수는 1 이상이어야 합니다.")
        @Max(value = 100, message = "조회 개수는 100 이하여야 합니다.")
        Integer limit
) {

    public GetRestaurantCommentsQuery toQuery(Long restaurantId) {
        return new GetRestaurantCommentsQuery(restaurantId, sort, limit);
    }
}
