package com.example.hyunjiinserver.user.restaurant.sync;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TourApiRestaurantSyncExecutorConfig {

    @Bean("tourApiSyncExecutor")
    public Executor tourApiSyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("tour-api-sync-");
        executor.initialize();
        return executor;
    }
}
