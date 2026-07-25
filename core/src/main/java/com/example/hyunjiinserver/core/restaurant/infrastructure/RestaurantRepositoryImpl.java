package com.example.hyunjiinserver.core.restaurant.infrastructure;

import com.example.hyunjiinserver.core.restaurant.domain.LocalComment;
import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantCommentSearchCondition;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantMapSearchCondition;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantQueryRepository restaurantQueryRepository;

    @Override
    public List<Restaurant> findByMapBounds(RestaurantMapSearchCondition condition) {
        return restaurantQueryRepository.findByMapBounds(condition);
    }

    @Override
    public Optional<Restaurant> findById(Long id) {
        return restaurantJpaRepository.findById(id);
    }

    @Override
    public Optional<Restaurant> findByTourContentId(String tourContentId) {
        return restaurantJpaRepository.findByTourContentId(tourContentId);
    }

    @Override
    public List<Restaurant> findByIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return restaurantJpaRepository.findAllById(ids);
    }

    @Override
    public List<LocalComment> findComments(RestaurantCommentSearchCondition condition) {
        return restaurantQueryRepository.findComments(condition);
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        return restaurantJpaRepository.save(restaurant);
    }
}
