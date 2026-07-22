package com.example.hyunjiinserver.user.report;

import com.example.hyunjiinserver.core.report.application.GetMyReportsQuery;
import com.example.hyunjiinserver.core.report.application.ReportService;
import com.example.hyunjiinserver.user.report.dto.MyReportsResponse;
import com.example.hyunjiinserver.user.report.dto.ReportSubmitRequest;
import com.example.hyunjiinserver.user.report.dto.ReportSubmitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController implements ReportApi {

    private final ReportService reportService;

    @Override
    public ReportSubmitResponse submitReport(String deviceId, Long restaurantId, ReportSubmitRequest request) {
        return ReportSubmitResponse.from(
                reportService.submitReport(request.toCommand(deviceId, restaurantId))
        );
    }

    @Override
    public MyReportsResponse getMyReports(String deviceId) {
        return MyReportsResponse.from(reportService.getMyReports(new GetMyReportsQuery(deviceId)));
    }
}
