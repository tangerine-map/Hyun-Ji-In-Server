package com.example.hyunjiinserver.core.report.application;

import java.util.List;

public record MyReportsResult(
        List<MyReportResult> reports
) {

    public MyReportsResult {
        reports = List.copyOf(reports);
    }
}
