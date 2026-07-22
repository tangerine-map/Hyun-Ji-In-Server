package com.example.hyunjiinserver.user.recommendation.dto;

import com.example.hyunjiinserver.core.recommendation.application.RecommendedRestaurantResult;
import io.swagger.v3.oas.annotations.media.Schema;

public record RecommendedRestaurantResponse(
        @Schema(description = "식당 ID입니다.", example = "1")
        Long restaurantId,

        @Schema(description = "식당명입니다.", example = "제주 흑돼지 현지인집")
        String name,

        @Schema(description = "대표 메뉴명입니다.", example = "흑돼지 구이")
        String representativeMenuName,

        @Schema(description = "대표 메뉴 가격입니다.", example = "18000")
        Integer representativeMenuPrice,

        @Schema(description = "식당 위치 위도입니다. 지도 마커 표시에 사용합니다.", example = "33.500912")
        double latitude,

        @Schema(description = "식당 위치 경도입니다. 지도 마커 표시에 사용합니다.", example = "126.529756")
        double longitude,

        @Schema(description = "현재 위치에서 식당까지의 거리입니다. 요청에 좌표가 없으면 null입니다. 단위는 미터입니다.", example = "430")
        Integer distanceMeters,

        @Schema(description = "추천 이유 한 줄입니다.", example = "조용한 분위기의 현지인 단골 흑돼지 맛집이에요.")
        String reason,

        @Schema(description = "가격 적정도 라벨입니다.", example = "가격 적정")
        String priceAdequacyLabel,

        @Schema(description = "현지인 추천 뱃지 노출 여부입니다.", example = "true")
        boolean localRecommended,

        @Schema(description = "현재 기기(X-Device-Id) 기준 저장 여부입니다.", example = "false")
        boolean saved
) {

    public static RecommendedRestaurantResponse from(RecommendedRestaurantResult result) {
        return new RecommendedRestaurantResponse(
                result.restaurantId(),
                result.name(),
                result.representativeMenuName(),
                result.representativeMenuPrice(),
                result.latitude(),
                result.longitude(),
                result.distanceMeters(),
                result.reason(),
                result.priceAdequacyLabel(),
                result.localRecommended(),
                result.saved()
        );
    }
}
