package com.example.hyunjiinserver.core.restaurant.domain;

public record RestaurantCommentSearchCondition(
        Long restaurantId,
        String sort,
        int limit
) {

    public boolean sortByHelpful() {
        return "helpful".equalsIgnoreCase(sort);
    }
}
