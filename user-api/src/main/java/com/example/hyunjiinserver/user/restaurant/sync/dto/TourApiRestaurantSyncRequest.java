package com.example.hyunjiinserver.user.restaurant.sync.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TourApiRestaurantSyncRequest(
        @NotNull(message = "가져올 식당 개수는 필수입니다.")
        @Min(value = 1, message = "가져올 식당 개수는 1 이상이어야 합니다.")
        @Max(value = 300, message = "한 번에 가져올 식당 개수는 300 이하여야 합니다.")
        Integer maxItems
) {
}
