package com.example.hyunjiinserver.core.search.domain;

public record SearchSuggestionResult(
        SearchSuggestionType type,
        String keyword,
        Long referenceId,
        String description
) {
}
