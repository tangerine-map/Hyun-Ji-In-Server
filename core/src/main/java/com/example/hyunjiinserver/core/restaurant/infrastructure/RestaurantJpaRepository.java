package com.example.hyunjiinserver.core.restaurant.infrastructure;

import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

interface RestaurantJpaRepository extends JpaRepository<Restaurant, Long> {
}
