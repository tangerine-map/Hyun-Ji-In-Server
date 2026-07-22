package com.example.hyunjiinserver.core.report.application;

import java.time.OffsetDateTime;

public record SubmitReportResult(
        Long reportId,
        String status,
        String statusDescription,
        OffsetDateTime createdAt
) {
}
