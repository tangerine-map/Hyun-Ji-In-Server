package com.example.hyunjiinserver.user.restaurant.dto;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantSummaryResult;
import io.swagger.v3.oas.annotations.media.Schema;

public record RestaurantSummaryResponse(
        @Schema(description = "식당 ID입니다. 상세 조회와 저장 토글에 사용합니다.", example = "1")
        Long id,

        @Schema(description = "식당명입니다.", example = "제주 고기국수 현지인집")
        String name,

        @Schema(description = "바텀시트 카드에 노출할 대표 메뉴명입니다.", example = "고기국수")
        String representativeMenuName,

        @Schema(description = "대표 메뉴 가격입니다. 가격 정보가 없으면 null입니다.", example = "9000")
        Integer representativeMenuPrice,

        @Schema(description = "식당 위치 위도입니다. 지도 마커 표시 위치로 사용합니다.", example = "33.500912")
        double latitude,

        @Schema(description = "식당 위치 경도입니다. 지도 마커 표시 위치로 사용합니다.", example = "126.529756")
        double longitude,

        @Schema(description = "요청 중심 좌표에서 식당까지의 거리입니다. 단위는 미터입니다.", example = "430")
        Integer distanceMeters,

        @Schema(description = "가격 적정도 라벨입니다. 예: 가격 적정, 저렴함, 비싼 편", example = "가격 적정")
        String priceAdequacyLabel,

        @Schema(description = "현지인 추천 뱃지 노출 여부입니다.", example = "true")
        boolean localRecommended,

        @Schema(description = "현재 사용자의 저장 여부입니다. 로그인/저장 기능 연동 전에는 false입니다.", example = "false")
        boolean saved
) {

    public static RestaurantSummaryResponse from(RestaurantSummaryResult result) {
        return new RestaurantSummaryResponse(
                result.id(),
                result.name(),
                result.representativeMenuName(),
                result.representativeMenuPrice(),
                result.latitude(),
                result.longitude(),
                result.distanceMeters(),
                result.priceAdequacyLabel(),
                result.localRecommended(),
                result.saved()
        );
    }
}
