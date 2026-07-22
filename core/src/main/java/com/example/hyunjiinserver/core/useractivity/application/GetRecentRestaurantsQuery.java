package com.example.hyunjiinserver.core.useractivity.application;

public record GetRecentRestaurantsQuery(
        String deviceId,
        Double latitude,
        Double longitude
) {
}
