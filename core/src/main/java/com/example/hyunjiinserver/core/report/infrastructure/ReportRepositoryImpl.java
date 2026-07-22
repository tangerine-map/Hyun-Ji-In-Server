package com.example.hyunjiinserver.core.report.infrastructure;

import com.example.hyunjiinserver.core.report.domain.ReportRepository;
import com.example.hyunjiinserver.core.report.domain.RestaurantReport;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final RestaurantReportJpaRepository restaurantReportJpaRepository;

    @Override
    public RestaurantReport save(RestaurantReport report) {
        return restaurantReportJpaRepository.save(report);
    }

    @Override
    public List<RestaurantReport> findAllByDeviceId(String deviceId) {
        return restaurantReportJpaRepository.findAllByDeviceIdOrderByCreatedAtDesc(deviceId);
    }
}
