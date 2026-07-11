package com.example.hyunjiinserver.core.restaurant.application;

import java.time.OffsetDateTime;

public record RestaurantCommentResult(
        Long id,
        String authorContext,
        String content,
        int helpfulCount,
        OffsetDateTime createdAt
) {
}
