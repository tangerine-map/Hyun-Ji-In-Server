package com.example.hyunjiinserver.core.search.application;

import com.example.hyunjiinserver.core.search.domain.SearchSuggestionRepository;
import com.example.hyunjiinserver.core.search.domain.SearchSuggestionResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchSuggestionService {

    private final SearchSuggestionRepository searchSuggestionRepository;

    public List<SearchSuggestionResult> getSuggestions(GetSearchSuggestionsQuery query) {
        return searchSuggestionRepository.findSuggestions(query.toCondition());
    }
}
