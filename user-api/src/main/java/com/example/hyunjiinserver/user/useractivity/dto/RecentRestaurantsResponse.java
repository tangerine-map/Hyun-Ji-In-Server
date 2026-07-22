package com.example.hyunjiinserver.user.useractivity.dto;

import com.example.hyunjiinserver.core.useractivity.application.RecentRestaurantsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record RecentRestaurantsResponse(
        @Schema(description = "최근 본 식당 목록입니다. 최신 열람순으로 정렬됩니다.")
        List<RecentRestaurantResponse> restaurants
) {

    public static RecentRestaurantsResponse from(RecentRestaurantsResult result) {
        return new RecentRestaurantsResponse(
                result.restaurants()
                        .stream()
                        .map(RecentRestaurantResponse::from)
                        .toList()
        );
    }
}
