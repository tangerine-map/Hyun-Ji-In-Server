package com.example.hyunjiinserver.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.hyunjiinserver")
@EntityScan(basePackages = "com.example.hyunjiinserver.core")
@EnableJpaRepositories(basePackages = "com.example.hyunjiinserver.core")
public class UserApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApiApplication.class, args);
    }
}
