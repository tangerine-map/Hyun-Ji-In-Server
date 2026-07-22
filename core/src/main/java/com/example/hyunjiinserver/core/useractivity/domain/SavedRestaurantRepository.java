package com.example.hyunjiinserver.core.useractivity.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SavedRestaurantRepository {

    Optional<SavedRestaurant> findByDeviceIdAndRestaurantId(String deviceId, Long restaurantId);

    List<SavedRestaurant> findAllByDeviceId(String deviceId);

    Set<Long> findSavedRestaurantIds(String deviceId, Collection<Long> restaurantIds);

    SavedRestaurant save(SavedRestaurant savedRestaurant);

    void delete(SavedRestaurant savedRestaurant);
}
