package com.example.hyunjiinserver.user.useractivity;

import com.example.hyunjiinserver.core.useractivity.application.SavedRestaurantService;
import com.example.hyunjiinserver.core.useractivity.application.ToggleSavedRestaurantCommand;
import com.example.hyunjiinserver.user.useractivity.dto.SavedRestaurantListRequest;
import com.example.hyunjiinserver.user.useractivity.dto.SavedRestaurantsResponse;
import com.example.hyunjiinserver.user.useractivity.dto.ToggleSavedRestaurantResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SavedRestaurantController implements SavedRestaurantApi {

    private final SavedRestaurantService savedRestaurantService;

    @Override
    public ToggleSavedRestaurantResponse toggleSavedRestaurant(String deviceId, Long restaurantId) {
        return ToggleSavedRestaurantResponse.from(
                savedRestaurantService.toggleSavedRestaurant(new ToggleSavedRestaurantCommand(deviceId, restaurantId))
        );
    }

    @Override
    public SavedRestaurantsResponse getSavedRestaurants(String deviceId, SavedRestaurantListRequest request) {
        return SavedRestaurantsResponse.from(
                savedRestaurantService.getSavedRestaurants(request.toQuery(deviceId))
        );
    }
}
