package com.example.hyunjiinserver.user.search;

import com.example.hyunjiinserver.core.search.domain.SearchSuggestionResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record SearchSuggestionsResponse(
        @Schema(description = "검색창 자동완성 추천어 목록입니다.")
        List<SearchSuggestionResponse> suggestions
) {

    public static SearchSuggestionsResponse from(List<SearchSuggestionResult> results) {
        return new SearchSuggestionsResponse(
                results.stream()
                        .map(SearchSuggestionResponse::from)
                        .toList()
        );
    }
}
