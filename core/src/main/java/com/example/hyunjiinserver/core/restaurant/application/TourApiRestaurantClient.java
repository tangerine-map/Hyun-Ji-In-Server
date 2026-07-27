package com.example.hyunjiinserver.core.restaurant.application;

public interface TourApiRestaurantClient {

    TourApiRestaurantPage fetchJejuRestaurants(String serviceKey, int pageNo, int maxItems);
}
