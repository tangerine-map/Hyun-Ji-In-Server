package com.example.hyunjiinserver.user.restaurant.dto;

import com.example.hyunjiinserver.core.restaurant.application.FindRestaurantsQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RestaurantMapSearchRequest(
        @Schema(description = "검색 중심 위도입니다. 앱 최초 실행 시 현재 위치 위도를 전달하고, 위치 권한이 거부되면 기본 지역 중심 위도를 전달합니다.", example = "33.499621")
        @NotNull(message = "중심 위도는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "중심 위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "중심 위도는 90 이하여야 합니다.")
        Double centerLatitude,

        @Schema(description = "검색 중심 경도입니다. 앱 최초 실행 시 현재 위치 경도를 전달하고, 지도 이동 후에는 현재 지도 중심 경도를 전달합니다.", example = "126.531188")
        @NotNull(message = "중심 경도는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "중심 경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "중심 경도는 180 이하여야 합니다.")
        Double centerLongitude,

        @Schema(description = "중심 좌표 기준 검색 반경입니다. 앱 실행 초기값은 3000m 정도를 권장하고, 지도 줌 레벨에 따라 조정할 수 있습니다.", example = "3000")
        @Min(value = 100, message = "검색 반경은 100m 이상이어야 합니다.")
        @Max(value = 50000, message = "검색 반경은 50km 이하여야 합니다.")
        Integer radiusMeters,

        @Schema(description = "식당명 또는 메뉴명 검색어입니다. 예: 고기국수, 흑돼지, 카페", example = "고기국수")
        String keyword,

        @Schema(description = "식당 카테고리입니다. 예: 음식점, 카페", example = "음식점")
        String category,

        @Schema(description = "현지인 추천 식당만 조회할지 여부입니다. true이면 현지인 추천 식당만 반환합니다.", example = "true")
        Boolean localRecommended,

        @Schema(description = "최대 조회 개수입니다. 값을 생략하면 서버 기본값 50개가 적용됩니다.", example = "20")
        @Min(value = 1, message = "조회 개수는 1 이상이어야 합니다.")
        @Max(value = 100, message = "조회 개수는 100 이하여야 합니다.")
        Integer limit
) {

    public FindRestaurantsQuery toQuery() {
        return new FindRestaurantsQuery(
                centerLatitude,
                centerLongitude,
                radiusMeters,
                keyword,
                category,
                localRecommended,
                limit
        );
    }
}
