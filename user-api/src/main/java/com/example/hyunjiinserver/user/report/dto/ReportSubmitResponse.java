package com.example.hyunjiinserver.user.report.dto;

import com.example.hyunjiinserver.core.report.application.SubmitReportResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ReportSubmitResponse(
        @Schema(description = "접수된 신고 ID입니다.", example = "1")
        Long reportId,

        @Schema(description = "신고 처리 상태 코드입니다.", example = "RECEIVED")
        String status,

        @Schema(description = "신고 처리 상태 표시 문구입니다.", example = "접수")
        String statusDescription,

        @Schema(description = "접수 완료 안내 문구입니다.", example = "신고가 접수되었습니다. 처리까지 시간이 걸릴 수 있습니다.")
        String guideMessage,

        @Schema(description = "신고 접수 시각입니다.", example = "2026-07-22T12:30:00+09:00")
        OffsetDateTime createdAt
) {

    private static final String GUIDE_MESSAGE = "신고가 접수되었습니다. 처리까지 시간이 걸릴 수 있습니다.";

    public static ReportSubmitResponse from(SubmitReportResult result) {
        return new ReportSubmitResponse(
                result.reportId(),
                result.status(),
                result.statusDescription(),
                GUIDE_MESSAGE,
                result.createdAt()
        );
    }
}
