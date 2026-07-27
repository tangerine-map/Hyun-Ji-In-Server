package com.example.hyunjiinserver.core.restaurant.application;

import java.util.List;

public interface TourApiRestaurantClient {

    List<TourApiRestaurantData> fetchJejuRestaurants(String serviceKey, int maxItems);
}
