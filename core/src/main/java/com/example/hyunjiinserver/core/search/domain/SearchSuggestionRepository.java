package com.example.hyunjiinserver.core.search.domain;

import java.util.List;

public interface SearchSuggestionRepository {

    List<SearchSuggestionResult> findSuggestions(SearchSuggestionSearchCondition condition);
}
