package com.example.hyunjiinserver.core.restaurant.application;

public interface RestaurantInformationEnrichmentClient {

    RestaurantInformationEnrichmentResult enrich(RestaurantEnrichmentPlan plan, int maxSources);
}
