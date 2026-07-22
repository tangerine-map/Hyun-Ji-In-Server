package com.example.hyunjiinserver.core.useractivity.infrastructure;

import com.example.hyunjiinserver.core.useractivity.domain.RecentViewedRestaurant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentViewedRestaurantJpaRepository extends JpaRepository<RecentViewedRestaurant, Long> {

    Optional<RecentViewedRestaurant> findByDeviceIdAndRestaurantId(String deviceId, Long restaurantId);

    List<RecentViewedRestaurant> findAllByDeviceIdOrderByViewedAtDesc(String deviceId);

    void deleteAllByDeviceId(String deviceId);
}
