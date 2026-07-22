package com.example.hyunjiinserver.core.report.application;

import java.time.OffsetDateTime;

public record MyReportResult(
        Long id,
        Long restaurantId,
        String restaurantName,
        String type,
        String typeDescription,
        String content,
        String status,
        String statusDescription,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
