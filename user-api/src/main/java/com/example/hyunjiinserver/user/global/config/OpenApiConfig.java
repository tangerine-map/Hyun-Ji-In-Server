package com.example.hyunjiinserver.user.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi(@Value("${openapi.server-url:}") String serverUrl) {
        OpenAPI openApi = new OpenAPI();
        if (StringUtils.hasText(serverUrl)) {
            openApi.setServers(List.of(new Server().url(serverUrl.trim())));
        }
        return openApi;
    }
}
