package com.example.hyunjiinserver.core.restaurant.application;

import java.util.List;

public interface TourApiRestaurantClient {

    List<TourApiRestaurantData> fetchJejuRestaurants(int maxItems);
}
