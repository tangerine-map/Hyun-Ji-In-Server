package com.example.hyunjiinserver.core.restaurant.domain;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    List<Restaurant> findByMapBounds(RestaurantMapSearchCondition condition);

    Optional<Restaurant> findById(Long id);

    List<LocalComment> findComments(RestaurantCommentSearchCondition condition);
}
