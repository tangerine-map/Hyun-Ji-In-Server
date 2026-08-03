package com.example.hyunjiinserver.core.restaurant.infrastructure;

import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

interface RestaurantJpaRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByTourContentId(String tourContentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select restaurant from Restaurant restaurant where restaurant.id = :id")
    Optional<Restaurant> findByIdForUpdate(@Param("id") Long id);
}
