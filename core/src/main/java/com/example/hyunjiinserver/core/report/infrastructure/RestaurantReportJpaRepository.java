package com.example.hyunjiinserver.core.report.infrastructure;

import com.example.hyunjiinserver.core.report.domain.RestaurantReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantReportJpaRepository extends JpaRepository<RestaurantReport, Long> {

    List<RestaurantReport> findAllByDeviceIdOrderByCreatedAtDesc(String deviceId);
}
