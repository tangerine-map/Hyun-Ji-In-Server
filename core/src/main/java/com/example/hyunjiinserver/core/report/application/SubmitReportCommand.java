package com.example.hyunjiinserver.core.report.application;

import com.example.hyunjiinserver.core.report.domain.ReportType;

public record SubmitReportCommand(
        String deviceId,
        Long restaurantId,
        ReportType type,
        String content
) {
}
