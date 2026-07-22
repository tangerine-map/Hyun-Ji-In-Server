package com.example.hyunjiinserver.core.useractivity.domain;

public enum SavedRestaurantSort {

    RECENT,
    DISTANCE;

    public static SavedRestaurantSort from(String value) {
        if ("distance".equalsIgnoreCase(value)) {
            return DISTANCE;
        }
        return RECENT;
    }
}
