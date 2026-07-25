package com.example.hyunjiinserver.core.restaurant.infrastructure;

class TourApiClientException extends RuntimeException {

    TourApiClientException(String message) {
        super(message);
    }

    TourApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
