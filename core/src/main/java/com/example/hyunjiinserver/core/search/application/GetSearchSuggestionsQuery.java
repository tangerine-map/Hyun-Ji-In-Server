package com.example.hyunjiinserver.core.search.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.search.domain.SearchErrorCode;
import com.example.hyunjiinserver.core.search.domain.SearchSuggestionSearchCondition;

public record GetSearchSuggestionsQuery(
        String keyword,
        Integer limit
) {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 20;

    public GetSearchSuggestionsQuery {
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException(SearchErrorCode.INVALID_SEARCH_KEYWORD);
        }

        keyword = keyword.trim();
        limit = limit == null ? DEFAULT_LIMIT : Math.min(limit, MAX_LIMIT);
    }

    public SearchSuggestionSearchCondition toCondition() {
        return new SearchSuggestionSearchCondition(keyword, limit);
    }
}
