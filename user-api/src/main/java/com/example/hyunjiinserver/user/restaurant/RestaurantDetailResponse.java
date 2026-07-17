package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantDetailResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record RestaurantDetailResponse(
        @Schema(description = "식당 ID입니다.", example = "1")
        Long id,

        @Schema(description = "식당명입니다.", example = "제주 고기국수 현지인집")
        String name,

        @Schema(description = "식당 카테고리입니다.", example = "음식점")
        String category,

        @Schema(description = "식당 주소입니다.", example = "제주특별자치도 제주시 중앙로 1")
        String address,

        @Schema(description = "식당 전화번호입니다.", example = "064-000-0000")
        String phoneNumber,

        @Schema(description = "영업시간 안내 문구입니다.", example = "10:00-20:00")
        String openingHours,

        @Schema(description = "식당 위치 위도입니다.", example = "33.500912")
        double latitude,

        @Schema(description = "식당 위치 경도입니다.", example = "126.529756")
        double longitude,

        @Schema(description = "식당 요약 설명입니다.", example = "현지인이 자주 찾는 고기국수 식당입니다.")
        String summary,

        @Schema(description = "현지인 추천 여부입니다.", example = "true")
        boolean localRecommended,

        @Schema(description = "현지인 추천 사유입니다.", example = "관광지 근처지만 가격과 맛이 안정적입니다.")
        String localRecommendationReason,

        @Schema(description = "가격 적정도 라벨입니다.", example = "가격 적정")
        String priceAdequacyLabel,

        @Schema(description = "가격 적정도 상세 설명입니다.", example = "주변 유사 메뉴 대비 평균 가격대입니다.")
        String priceAdequacyDescription,

        @Schema(description = "현재 사용자의 저장 여부입니다. 로그인/저장 기능 연동 전에는 false입니다.", example = "false")
        boolean saved,

        @Schema(description = "대표 메뉴 목록입니다.")
        List<RestaurantMenuResponse> representativeMenus
) {

    public static RestaurantDetailResponse from(RestaurantDetailResult result) {
        return new RestaurantDetailResponse(
                result.id(),
                result.name(),
                result.category(),
                result.address(),
                result.phoneNumber(),
                result.openingHours(),
                result.latitude(),
                result.longitude(),
                result.summary(),
                result.localRecommended(),
                result.localRecommendationReason(),
                result.priceAdequacyLabel(),
                result.priceAdequacyDescription(),
                result.saved(),
                result.representativeMenus()
                        .stream()
                        .map(RestaurantMenuResponse::from)
                        .toList()
        );
    }
}
