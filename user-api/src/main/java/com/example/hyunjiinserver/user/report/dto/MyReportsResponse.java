package com.example.hyunjiinserver.user.report.dto;

import com.example.hyunjiinserver.core.report.application.MyReportsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MyReportsResponse(
        @Schema(description = "내가 제출한 신고 목록입니다. 최신순으로 정렬됩니다.")
        List<MyReportResponse> reports
) {

    public static MyReportsResponse from(MyReportsResult result) {
        return new MyReportsResponse(
                result.reports()
                        .stream()
                        .map(MyReportResponse::from)
                        .toList()
        );
    }
}
