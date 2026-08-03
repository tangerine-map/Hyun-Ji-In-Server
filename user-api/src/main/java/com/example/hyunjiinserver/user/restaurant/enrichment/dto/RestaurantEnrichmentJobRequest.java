package com.example.hyunjiinserver.user.restaurant.enrichment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record RestaurantEnrichmentJobRequest(
        @Schema(description = "누락 정보를 보강할 식당 ID 목록입니다. 중복 ID는 한 번만 처리합니다.", example = "[2, 3, 4, 5]")
        @NotNull(message = "식당 ID 목록은 필수입니다.")
        @Size(min = 1, max = 100, message = "식당은 한 번에 1개 이상 100개 이하로 요청해야 합니다.")
        List<@NotNull(message = "식당 ID에는 null을 사용할 수 없습니다.")
                @Positive(message = "식당 ID는 양수여야 합니다.") Long> restaurantIds
) {
}
