package com.example.hyunjiinserver.core.restaurant.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "restaurant-enrichment")
public class RestaurantEnrichmentProperties {

    private int maxSourcesPerRestaurant = 5;
    private int parallelism = 3;
    private String crawlerBaseUrl = "http://localhost:8001";
}
