package com.example.hyunjiinserver.core.report.domain;

import java.util.List;

public interface ReportRepository {

    RestaurantReport save(RestaurantReport report);

    List<RestaurantReport> findAllByDeviceId(String deviceId);
}
