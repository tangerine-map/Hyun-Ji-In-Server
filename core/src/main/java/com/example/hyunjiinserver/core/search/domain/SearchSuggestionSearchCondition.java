package com.example.hyunjiinserver.core.search.domain;

public record SearchSuggestionSearchCondition(
        String keyword,
        int limit
) {
}
