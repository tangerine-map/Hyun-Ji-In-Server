package com.example.hyunjiinserver.core.useractivity.domain;

import java.util.List;
import java.util.Optional;

public interface RecentViewedRestaurantRepository {

    Optional<RecentViewedRestaurant> findByDeviceIdAndRestaurantId(String deviceId, Long restaurantId);

    List<RecentViewedRestaurant> findAllByDeviceId(String deviceId);

    RecentViewedRestaurant save(RecentViewedRestaurant recentViewedRestaurant);

    void delete(RecentViewedRestaurant recentViewedRestaurant);

    void deleteAll(List<RecentViewedRestaurant> recentViewedRestaurants);

    void deleteAllByDeviceId(String deviceId);
}
