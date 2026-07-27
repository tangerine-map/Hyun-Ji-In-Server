package com.example.hyunjiinserver.core.restaurant.application;

import java.util.List;

public record TourApiRestaurantPage(
        List<TourApiRestaurantData> restaurants,
        int pageNo,
        Integer nextPageNo
) {

    public TourApiRestaurantPage {
        restaurants = List.copyOf(restaurants);
    }
}
