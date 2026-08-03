package com.example.hyunjiinserver.user.restaurant.enrichment;

import com.example.hyunjiinserver.core.restaurant.infrastructure.RestaurantEnrichmentProperties;
import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class RestaurantEnrichmentExecutorConfig {

    @Bean("restaurantEnrichmentExecutor")
    public Executor restaurantEnrichmentExecutor(RestaurantEnrichmentProperties properties) {
        int parallelism = Math.max(1, Math.min(properties.getParallelism(), 10));
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(parallelism);
        executor.setMaxPoolSize(parallelism);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("restaurant-enrichment-");
        executor.initialize();
        return executor;
    }
}
