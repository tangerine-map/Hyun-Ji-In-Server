package com.example.hyunjiinserver.user.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    void configuresExplicitServerUrl() {
        OpenAPI openApi = config.openApi(" https://hyunjiin.site ");

        assertEquals(1, openApi.getServers().size());
        assertEquals("https://hyunjiin.site", openApi.getServers().getFirst().getUrl());
    }

    @Test
    void leavesServersEmptyWhenUrlIsNotConfigured() {
        OpenAPI openApi = config.openApi(" ");

        assertNull(openApi.getServers());
    }
}
