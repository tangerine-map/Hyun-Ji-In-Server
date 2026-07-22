package com.example.hyunjiinserver.user.useractivity.dto;

import com.example.hyunjiinserver.core.useractivity.application.ToggleSavedRestaurantResult;
import io.swagger.v3.oas.annotations.media.Schema;

public record ToggleSavedRestaurantResponse(
        @Schema(description = "저장 상태를 변경한 식당 ID입니다.", example = "1")
        Long restaurantId,

        @Schema(description = "토글 후 저장 여부입니다. true이면 저장됨, false이면 저장 해제됨을 의미합니다.", example = "true")
        boolean saved
) {

    public static ToggleSavedRestaurantResponse from(ToggleSavedRestaurantResult result) {
        return new ToggleSavedRestaurantResponse(result.restaurantId(), result.saved());
    }
}
