package com.example.hyunjiinserver.core.restaurant.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    List<Restaurant> findByMapBounds(RestaurantMapSearchCondition condition);

    Optional<Restaurant> findById(Long id);

    Optional<Restaurant> findByTourContentId(String tourContentId);

    List<Restaurant> findByIds(Collection<Long> ids);

    List<LocalComment> findComments(RestaurantCommentSearchCondition condition);

    Restaurant save(Restaurant restaurant);
}
