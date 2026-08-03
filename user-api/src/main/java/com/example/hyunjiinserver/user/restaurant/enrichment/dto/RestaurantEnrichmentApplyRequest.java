package com.example.hyunjiinserver.user.restaurant.enrichment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record RestaurantEnrichmentApplyRequest(
        @Schema(description = "DB에 반영할 보강 후보 ID 목록입니다.", example = "[12, 15, 18]")
        @NotNull(message = "후보 ID 목록은 필수입니다.")
        @Size(min = 1, max = 500, message = "후보는 한 번에 1개 이상 500개 이하로 반영해야 합니다.")
        List<@NotNull(message = "후보 ID에는 null을 사용할 수 없습니다.")
                @Positive(message = "후보 ID는 양수여야 합니다.") Long> candidateIds
) {
}
