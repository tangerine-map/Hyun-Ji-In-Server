package com.example.hyunjiinserver.user.useractivity.dto;

import com.example.hyunjiinserver.core.useractivity.application.SavedRestaurantResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record SavedRestaurantResponse(
        @Schema(description = "식당 ID입니다.", example = "1")
        Long restaurantId,

        @Schema(description = "식당명입니다.", example = "제주 고기국수 현지인집")
        String name,

        @Schema(description = "대표 메뉴명입니다. 대표 메뉴 정보가 없으면 null입니다.", example = "고기국수")
        String representativeMenuName,

        @Schema(description = "대표 메뉴 가격입니다. 가격 정보가 없으면 null입니다.", example = "9000")
        Integer representativeMenuPrice,

        @Schema(description = "식당 위치 위도입니다.", example = "33.500912")
        double latitude,

        @Schema(description = "식당 위치 경도입니다.", example = "126.529756")
        double longitude,

        @Schema(description = "현재 위치에서 식당까지의 거리입니다. 요청에 좌표가 없으면 null입니다. 단위는 미터입니다.", example = "430")
        Integer distanceMeters,

        @Schema(description = "가격 적정도 라벨입니다. 예: 평균보다 저렴, 가격 적정, 평균보다 높은 편", example = "가격 적정")
        String priceAdequacyLabel,

        @Schema(description = "현지인 추천 뱃지 노출 여부입니다.", example = "true")
        boolean localRecommended,

        @Schema(description = "저장한 시각입니다.", example = "2026-07-21T12:30:00+09:00")
        OffsetDateTime savedAt
) {

    public static SavedRestaurantResponse from(SavedRestaurantResult result) {
        return new SavedRestaurantResponse(
                result.restaurantId(),
                result.name(),
                result.representativeMenuName(),
                result.representativeMenuPrice(),
                result.latitude(),
                result.longitude(),
                result.distanceMeters(),
                result.priceAdequacyLabel(),
                result.localRecommended(),
                result.savedAt()
        );
    }
}
