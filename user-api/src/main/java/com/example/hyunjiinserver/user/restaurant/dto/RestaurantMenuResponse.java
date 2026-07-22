package com.example.hyunjiinserver.user.restaurant.dto;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantMenuResult;
import io.swagger.v3.oas.annotations.media.Schema;

public record RestaurantMenuResponse(
        @Schema(description = "메뉴 ID입니다.", example = "10")
        Long id,

        @Schema(description = "메뉴명입니다.", example = "고기국수")
        String name,

        @Schema(description = "메뉴 가격입니다.", example = "9000")
        Integer price,

        @Schema(description = "대표 메뉴 여부입니다.", example = "true")
        boolean representative
) {

    public static RestaurantMenuResponse from(RestaurantMenuResult result) {
        return new RestaurantMenuResponse(
                result.id(),
                result.name(),
                result.price(),
                result.representative()
        );
    }
}
