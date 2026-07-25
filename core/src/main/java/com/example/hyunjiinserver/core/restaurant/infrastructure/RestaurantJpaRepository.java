package com.example.hyunjiinserver.core.restaurant.infrastructure;

import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

interface RestaurantJpaRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByTourContentId(String tourContentId);
}
