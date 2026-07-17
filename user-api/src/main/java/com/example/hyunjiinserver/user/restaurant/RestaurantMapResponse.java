package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantMapResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record RestaurantMapResponse(
        @Schema(description = "지도 마커와 바텀시트 리스트에 동일하게 사용할 식당 목록입니다.")
        List<RestaurantSummaryResponse> restaurants
) {

    public static RestaurantMapResponse from(RestaurantMapResult result) {
        return new RestaurantMapResponse(
                result.restaurants()
                        .stream()
                        .map(RestaurantSummaryResponse::from)
                        .toList()
        );
    }
}
