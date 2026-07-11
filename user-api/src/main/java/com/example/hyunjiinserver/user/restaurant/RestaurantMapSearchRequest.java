package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.core.restaurant.application.FindRestaurantsQuery;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RestaurantMapSearchRequest(
        @NotNull(message = "남서쪽 위도는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "남서쪽 위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "남서쪽 위도는 90 이하여야 합니다.")
        Double southWestLatitude,

        @NotNull(message = "남서쪽 경도는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "남서쪽 경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "남서쪽 경도는 180 이하여야 합니다.")
        Double southWestLongitude,

        @NotNull(message = "북동쪽 위도는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "북동쪽 위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "북동쪽 위도는 90 이하여야 합니다.")
        Double northEastLatitude,

        @NotNull(message = "북동쪽 경도는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "북동쪽 경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "북동쪽 경도는 180 이하여야 합니다.")
        Double northEastLongitude,

        @DecimalMin(value = "-90.0", message = "사용자 위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "사용자 위도는 90 이하여야 합니다.")
        Double userLatitude,

        @DecimalMin(value = "-180.0", message = "사용자 경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "사용자 경도는 180 이하여야 합니다.")
        Double userLongitude,

        String keyword,
        String category,
        Boolean localRecommended,

        @Min(value = 1, message = "조회 개수는 1 이상이어야 합니다.")
        @Max(value = 100, message = "조회 개수는 100 이하여야 합니다.")
        Integer limit
) {

    public FindRestaurantsQuery toQuery() {
        return new FindRestaurantsQuery(
                southWestLatitude,
                southWestLongitude,
                northEastLatitude,
                northEastLongitude,
                userLatitude,
                userLongitude,
                keyword,
                category,
                localRecommended,
                limit
        );
    }
}
