package com.example.hyunjiinserver.user.search;

import com.example.hyunjiinserver.core.search.application.SearchSuggestionService;
import com.example.hyunjiinserver.core.search.domain.SearchSuggestionResult;
import com.example.hyunjiinserver.user.search.dto.SearchSuggestionRequest;
import com.example.hyunjiinserver.user.search.dto.SearchSuggestionsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController implements SearchApi {

    private final SearchSuggestionService searchSuggestionService;

    @Override
    public SearchSuggestionsResponse getSuggestions(SearchSuggestionRequest request) {
        List<SearchSuggestionResult> results = searchSuggestionService.getSuggestions(request.toQuery());
        return SearchSuggestionsResponse.from(results);
    }
}
