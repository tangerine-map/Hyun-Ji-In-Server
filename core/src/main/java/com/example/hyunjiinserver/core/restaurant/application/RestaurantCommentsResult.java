package com.example.hyunjiinserver.core.restaurant.application;

import java.util.List;

public record RestaurantCommentsResult(
        List<RestaurantCommentResult> comments
) {

    public RestaurantCommentsResult {
        comments = List.copyOf(comments);
    }
}
