package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantCommentResult;
import java.time.OffsetDateTime;

public record RestaurantCommentResponse(
        Long id,
        String authorContext,
        String content,
        int helpfulCount,
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
