package com.example.hyunjiinserver.user.search;

import com.example.hyunjiinserver.core.search.application.GetSearchSuggestionsQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SearchSuggestionRequest(
        @Schema(description = "자동완성 기준 검색어입니다. 식당명 또는 메뉴명을 기준으로 추천어를 조회합니다.", example = "고기")
        @NotBlank(message = "검색어는 필수입니다.")
        String keyword,

        @Schema(description = "최대 조회 개수입니다. 생략하면 10개, 최대 20개까지 조회합니다.", example = "10")
        @Min(value = 1, message = "조회 개수는 1 이상이어야 합니다.")
        @Max(value = 20, message = "조회 개수는 20 이하여야 합니다.")
        Integer limit
) {

    public GetSearchSuggestionsQuery toQuery() {
        return new GetSearchSuggestionsQuery(keyword, limit);
    }
}
