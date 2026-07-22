package com.example.hyunjiinserver.user.useractivity.dto;

import com.example.hyunjiinserver.core.useractivity.application.GetRecentRestaurantsQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record RecentRestaurantListRequest(
        @Schema(description = "현재 위치 위도입니다. 전달하면 각 식당까지의 거리가 함께 반환됩니다.", example = "33.499621")
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        Double latitude,

        @Schema(description = "현재 위치 경도입니다. 전달하면 각 식당까지의 거리가 함께 반환됩니다.", example = "126.531188")
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        Double longitude
) {

    public GetRecentRestaurantsQuery toQuery(String deviceId) {
        return new GetRecentRestaurantsQuery(deviceId, latitude, longitude);
    }
}
