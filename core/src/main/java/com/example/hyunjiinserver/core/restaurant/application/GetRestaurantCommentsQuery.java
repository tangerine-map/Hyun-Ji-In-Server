package com.example.hyunjiinserver.core.restaurant.application;

import com.example.hyunjiinserver.core.restaurant.domain.RestaurantCommentSearchCondition;

public record GetRestaurantCommentsQuery(
        Long restaurantId,
        String sort,
        Integer limit
) {

    private static final int DEFAULT_LIMIT = 20;

    public GetRestaurantCommentsQuery {
        limit = limit == null ? DEFAULT_LIMIT : limit;
    }

    public RestaurantCommentSearchCondition toCondition() {
        return new RestaurantCommentSearchCondition(restaurantId, sort, limit);
    }
}
