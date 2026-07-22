package com.example.hyunjiinserver.core.useractivity.infrastructure;

import com.example.hyunjiinserver.core.useractivity.domain.SavedRestaurant;
import com.example.hyunjiinserver.core.useractivity.domain.SavedRestaurantRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SavedRestaurantRepositoryImpl implements SavedRestaurantRepository {

    private final SavedRestaurantJpaRepository savedRestaurantJpaRepository;

    @Override
    public Optional<SavedRestaurant> findByDeviceIdAndRestaurantId(String deviceId, Long restaurantId) {
        return savedRestaurantJpaRepository.findByDeviceIdAndRestaurantId(deviceId, restaurantId);
    }

    @Override
    public List<SavedRestaurant> findAllByDeviceId(String deviceId) {
        return savedRestaurantJpaRepository.findAllByDeviceIdOrderBySavedAtDesc(deviceId);
    }

    @Override
    public Set<Long> findSavedRestaurantIds(String deviceId, Collection<Long> restaurantIds) {
        if (restaurantIds.isEmpty()) {
            return Set.of();
        }
        return savedRestaurantJpaRepository.findAllByDeviceIdAndRestaurantIdIn(deviceId, restaurantIds)
                .stream()
                .map(SavedRestaurant::getRestaurantId)
                .collect(Collectors.toSet());
    }

    @Override
    public SavedRestaurant save(SavedRestaurant savedRestaurant) {
        return savedRestaurantJpaRepository.save(savedRestaurant);
    }

    @Override
    public void delete(SavedRestaurant savedRestaurant) {
        savedRestaurantJpaRepository.delete(savedRestaurant);
    }
}
