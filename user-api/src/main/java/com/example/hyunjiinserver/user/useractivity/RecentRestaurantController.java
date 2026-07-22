package com.example.hyunjiinserver.user.useractivity;

import com.example.hyunjiinserver.core.useractivity.application.RecentViewedRestaurantService;
import com.example.hyunjiinserver.user.useractivity.dto.RecentRestaurantListRequest;
import com.example.hyunjiinserver.user.useractivity.dto.RecentRestaurantsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecentRestaurantController implements RecentRestaurantApi {

    private final RecentViewedRestaurantService recentViewedRestaurantService;

    @Override
    public RecentRestaurantsResponse getRecentRestaurants(String deviceId, RecentRestaurantListRequest request) {
        return RecentRestaurantsResponse.from(
                recentViewedRestaurantService.getRecentRestaurants(request.toQuery(deviceId))
        );
    }

    @Override
    public void deleteRecentRestaurant(String deviceId, Long restaurantId) {
        recentViewedRestaurantService.deleteRecentView(deviceId, restaurantId);
    }

    @Override
    public void clearRecentRestaurants(String deviceId) {
        recentViewedRestaurantService.clearRecentViews(deviceId);
    }
}
