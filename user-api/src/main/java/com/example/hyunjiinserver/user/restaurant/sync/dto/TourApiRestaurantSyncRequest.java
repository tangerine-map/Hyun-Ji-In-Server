package com.example.hyunjiinserver.user.restaurant.sync.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TourApiRestaurantSyncRequest(
        @Schema(description = "조회할 TourAPI 페이지 번호입니다. 생략하면 1페이지를 조회합니다.", defaultValue = "1", example = "1")
        @Min(value = 1, message = "조회할 페이지 번호는 1 이상이어야 합니다.")
        Integer pageNo,

        @Schema(description = "한 페이지에서 가져올 최대 식당 수입니다.", example = "100")
        @NotNull(message = "가져올 식당 개수는 필수입니다.")
        @Min(value = 1, message = "가져올 식당 개수는 1 이상이어야 합니다.")
        @Max(value = 100, message = "한 번에 가져올 식당 개수는 100 이하여야 합니다.")
        Integer maxItems
) {

    public int resolvedPageNo() {
        return pageNo == null ? 1 : pageNo;
    }
}
