package com.example.hyunjiinserver.core.restaurant.infrastructure;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class TourApiRestaurantClientImplTest {

    @Test
    void encodesDecodingServiceKeyAsQueryParameter() throws IOException {
        AtomicReference<String> rawQuery = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/areaBasedList2", exchange -> {
            rawQuery.set(exchange.getRequestURI().getRawQuery());
            byte[] response = """
                    {"response":{"header":{"resultCode":"0000","resultMsg":"OK"},"body":{"totalCount":0,"items":{}}}}
                    """.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();

        try {
            TourApiProperties properties = new TourApiProperties();
            properties.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
            properties.setPageSize(1);
            TourApiRestaurantClientImpl client = new TourApiRestaurantClientImpl(
                    properties,
                    new TourApiResponseParser(),
                    new TourApiRestaurantMapper()
            );

            client.fetchJejuRestaurants("abc+def/ghi==", 1);

            assertTrue(rawQuery.get().contains("serviceKey=abc%2Bdef%2Fghi%3D%3D"));
        } finally {
            server.stop(0);
        }
    }
}
