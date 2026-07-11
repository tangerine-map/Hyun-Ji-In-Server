package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantCommentsResult;
import java.util.List;

public record RestaurantCommentsResponse(
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
