package com.example.hyunjiinserver.core.useractivity.infrastructure;

import com.example.hyunjiinserver.core.useractivity.domain.RecentViewedRestaurant;
import com.example.hyunjiinserver.core.useractivity.domain.RecentViewedRestaurantRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecentViewedRestaurantRepositoryImpl implements RecentViewedRestaurantRepository {

    private final RecentViewedRestaurantJpaRepository recentViewedRestaurantJpaRepository;

    @Override
    public Optional<RecentViewedRestaurant> findByDeviceIdAndRestaurantId(String deviceId, Long restaurantId) {
        return recentViewedRestaurantJpaRepository.findByDeviceIdAndRestaurantId(deviceId, restaurantId);
    }

    @Override
    public List<RecentViewedRestaurant> findAllByDeviceId(String deviceId) {
        return recentViewedRestaurantJpaRepository.findAllByDeviceIdOrderByViewedAtDesc(deviceId);
    }

    @Override
    public RecentViewedRestaurant save(RecentViewedRestaurant recentViewedRestaurant) {
        return recentViewedRestaurantJpaRepository.save(recentViewedRestaurant);
    }

    @Override
    public void delete(RecentViewedRestaurant recentViewedRestaurant) {
        recentViewedRestaurantJpaRepository.delete(recentViewedRestaurant);
    }

    @Override
    public void deleteAll(List<RecentViewedRestaurant> recentViewedRestaurants) {
        recentViewedRestaurantJpaRepository.deleteAll(recentViewedRestaurants);
    }

    @Override
    public void deleteAllByDeviceId(String deviceId) {
        recentViewedRestaurantJpaRepository.deleteAllByDeviceId(deviceId);
    }
}
