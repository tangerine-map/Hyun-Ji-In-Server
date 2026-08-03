package com.example.hyunjiinserver.core.restaurant.infrastructure;

public class RestaurantEnrichmentClientException extends RuntimeException {

    public RestaurantEnrichmentClientException(String message) {
        super(message);
    }

    public RestaurantEnrichmentClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
