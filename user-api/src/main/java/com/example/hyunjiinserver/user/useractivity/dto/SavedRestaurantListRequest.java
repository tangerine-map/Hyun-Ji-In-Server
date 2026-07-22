package com.example.hyunjiinserver.user.useractivity.dto;

import com.example.hyunjiinserver.core.useractivity.application.GetSavedRestaurantsQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record SavedRestaurantListRequest(
        @Schema(
                description = "정렬 기준입니다. 최근 저장순 recent, 거리순 distance. 생략하면 최근 저장순으로 정렬합니다. 거리순은 latitude, longitude가 필요합니다.",
                example = "recent"
        )
        String sort,

        @Schema(description = "현지인 추천 식당만 조회할지 여부입니다. 생략하면 전체를 조회합니다.", example = "false")
        Boolean localRecommendedOnly,

        @Schema(description = "가격 적정(평균보다 저렴 포함) 식당만 조회할지 여부입니다. 생략하면 전체를 조회합니다.", example = "false")
        Boolean priceAdequateOnly,

        @Schema(description = "현재 위치 위도입니다. 거리순 정렬 또는 거리 표시에 사용합니다.", example = "33.499621")
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
        Double latitude,

        @Schema(description = "현재 위치 경도입니다. 거리순 정렬 또는 거리 표시에 사용합니다.", example = "126.531188")
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
        Double longitude
) {

    public GetSavedRestaurantsQuery toQuery(String deviceId) {
        return new GetSavedRestaurantsQuery(
                deviceId,
                sort,
                Boolean.TRUE.equals(localRecommendedOnly),
                Boolean.TRUE.equals(priceAdequateOnly),
                latitude,
                longitude
        );
    }
}
