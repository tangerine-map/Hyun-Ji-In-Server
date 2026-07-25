package com.example.hyunjiinserver.core.global;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreTimeConfiguration {

    @Bean
    @ConditionalOnMissingBean(Clock.class)
    Clock serviceClock() {
        return Clock.system(ZoneId.of("Asia/Seoul"));
    }
}
