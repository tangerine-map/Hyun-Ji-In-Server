package com.example.hyunjiinserver.user.search;

import com.example.hyunjiinserver.core.search.domain.SearchSuggestionResult;
import com.example.hyunjiinserver.core.search.domain.SearchSuggestionType;
import io.swagger.v3.oas.annotations.media.Schema;

public record SearchSuggestionResponse(
        @Schema(description = "추천어 유형입니다. REGION, TOURIST_ATTRACTION은 해당 데이터 도메인 추가 후 사용합니다.", example = "MENU")
        SearchSuggestionType type,

        @Schema(description = "검색창에 표시하고, 선택 시 지도 검색 keyword로 사용할 문구입니다.", example = "고기국수")
        String keyword,

        @Schema(description = "추천어가 특정 데이터에 연결될 때 사용하는 ID입니다. 메뉴/지역/관광지처럼 키워드형 추천어는 null일 수 있습니다.", example = "1")
        Long referenceId,

        @Schema(description = "추천어 보조 설명입니다. 식당 추천어는 카테고리, 메뉴 추천어는 '메뉴'를 제공합니다.", example = "메뉴")
        String description
) {

    public static SearchSuggestionResponse from(SearchSuggestionResult result) {
        return new SearchSuggestionResponse(
                result.type(),
                result.keyword(),
                result.referenceId(),
                result.description()
        );
    }
}
