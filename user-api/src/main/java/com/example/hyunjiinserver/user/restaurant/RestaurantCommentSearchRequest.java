package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.core.restaurant.application.GetRestaurantCommentsQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RestaurantCommentSearchRequest(
        String sort,

        @Min(value = 1, message = "조회 개수는 1 이상이어야 합니다.")
        @Max(value = 100, message = "조회 개수는 100 이하여야 합니다.")
        Integer limit
) {

    public GetRestaurantCommentsQuery toQuery(Long restaurantId) {
        return new GetRestaurantCommentsQuery(restaurantId, sort, limit);
    }
}
