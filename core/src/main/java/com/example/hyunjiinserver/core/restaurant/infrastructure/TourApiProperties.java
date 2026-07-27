package com.example.hyunjiinserver.core.restaurant.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "tour-api")
public class TourApiProperties {

    private String baseUrl = "https://apis.data.go.kr/B551011/KorService2";
    private String mobileOs = "ETC";
    private String mobileApp = "hyunjiin";
    private int areaCode = 39;
    private int contentTypeId = 39;
}
