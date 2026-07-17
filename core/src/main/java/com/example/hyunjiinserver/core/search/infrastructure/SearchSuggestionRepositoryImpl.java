package com.example.hyunjiinserver.core.search.infrastructure;

import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantStatus;
import com.example.hyunjiinserver.core.search.domain.SearchSuggestionRepository;
import com.example.hyunjiinserver.core.search.domain.SearchSuggestionResult;
import com.example.hyunjiinserver.core.search.domain.SearchSuggestionSearchCondition;
import com.example.hyunjiinserver.core.search.domain.SearchSuggestionType;
import jakarta.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SearchSuggestionRepositoryImpl implements SearchSuggestionRepository {

    private final EntityManager entityManager;

    @Override
    public List<SearchSuggestionResult> findSuggestions(SearchSuggestionSearchCondition condition) {
        int restaurantLimit = Math.max(1, condition.limit());
        int menuLimit = Math.max(1, condition.limit());

        List<SearchSuggestionResult> restaurantSuggestions = entityManager.createQuery("""
                        select r
                        from Restaurant r
                        where r.status = :status
                          and r.name like concat('%', :keyword, '%')
                        order by r.localRecommended desc, r.name asc
                        """, Restaurant.class)
                .setParameter("status", RestaurantStatus.OPEN)
                .setParameter("keyword", condition.keyword())
                .setMaxResults(restaurantLimit)
                .getResultList()
                .stream()
                .map(restaurant -> new SearchSuggestionResult(
                        SearchSuggestionType.RESTAURANT,
                        restaurant.getName(),
                        restaurant.getId(),
                        restaurant.getCategory()
                ))
                .toList();

        List<SearchSuggestionResult> menuSuggestions = entityManager.createQuery("""
                        select distinct m.name
                        from RestaurantMenu m
                        join m.restaurant r
                        where r.status = :status
                          and m.name like concat('%', :keyword, '%')
                        order by m.name asc
                        """, String.class)
                .setParameter("status", RestaurantStatus.OPEN)
                .setParameter("keyword", condition.keyword())
                .setMaxResults(menuLimit)
                .getResultList()
                .stream()
                .map(menuName -> new SearchSuggestionResult(
                        SearchSuggestionType.MENU,
                        menuName,
                        null,
                        "메뉴"
                ))
                .toList();

        return java.util.stream.Stream.concat(restaurantSuggestions.stream(), menuSuggestions.stream())
                .sorted(Comparator
                        .comparing((SearchSuggestionResult suggestion) -> suggestion.keyword().startsWith(condition.keyword()) ? 0 : 1)
                        .thenComparing(suggestion -> suggestion.type().ordinal())
                        .thenComparing(SearchSuggestionResult::keyword))
                .limit(condition.limit())
                .toList();
    }
}
