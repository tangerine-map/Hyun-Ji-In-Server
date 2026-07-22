package com.example.hyunjiinserver.core.report.application;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import com.example.hyunjiinserver.core.report.domain.ReportRepository;
import com.example.hyunjiinserver.core.report.domain.RestaurantReport;
import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantErrorCode;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public SubmitReportResult submitReport(SubmitReportCommand command) {
        restaurantRepository.findById(command.restaurantId())
                .orElseThrow(() -> new BusinessException(RestaurantErrorCode.RESTAURANT_NOT_FOUND));

        RestaurantReport report = reportRepository.save(
                new RestaurantReport(command.deviceId(), command.restaurantId(), command.type(), command.content())
        );

        return new SubmitReportResult(
                report.getId(),
                report.getStatus().name(),
                report.getStatus().description(),
                report.getCreatedAt()
        );
    }

    public MyReportsResult getMyReports(GetMyReportsQuery query) {
        var reports = reportRepository.findAllByDeviceId(query.deviceId());

        Map<Long, Restaurant> restaurantsById = restaurantRepository.findByIds(
                        reports.stream().map(RestaurantReport::getRestaurantId).distinct().toList()
                )
                .stream()
                .collect(Collectors.toMap(Restaurant::getId, Function.identity()));

        return new MyReportsResult(
                reports.stream()
                        .map(report -> toResult(report, restaurantsById.get(report.getRestaurantId())))
                        .toList()
        );
    }

    private MyReportResult toResult(RestaurantReport report, Restaurant restaurant) {
        return new MyReportResult(
                report.getId(),
                report.getRestaurantId(),
                restaurant == null ? null : restaurant.getName(),
                report.getType().name(),
                report.getType().description(),
                report.getContent(),
                report.getStatus().name(),
                report.getStatus().description(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}
