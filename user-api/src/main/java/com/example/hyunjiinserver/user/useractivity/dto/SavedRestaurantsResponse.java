package com.example.hyunjiinserver.user.useractivity.dto;

import com.example.hyunjiinserver.core.useractivity.application.SavedRestaurantsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record SavedRestaurantsResponse(
        @Schema(description = "저장한 식당 목록입니다.")
        List<SavedRestaurantResponse> restaurants
) {

    public static SavedRestaurantsResponse from(SavedRestaurantsResult result) {
        return new SavedRestaurantsResponse(
                result.restaurants()
                        .stream()
                        .map(SavedRestaurantResponse::from)
                        .toList()
        );
    }
}
