package com.example.hyunjiinserver.core.useractivity.infrastructure;

import com.example.hyunjiinserver.core.useractivity.domain.SavedRestaurant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedRestaurantJpaRepository extends JpaRepository<SavedRestaurant, Long> {

    Optional<SavedRestaurant> findByDeviceIdAndRestaurantId(String deviceId, Long restaurantId);

    List<SavedRestaurant> findAllByDeviceIdOrderBySavedAtDesc(String deviceId);

    List<SavedRestaurant> findAllByDeviceIdAndRestaurantIdIn(String deviceId, Collection<Long> restaurantIds);
}
