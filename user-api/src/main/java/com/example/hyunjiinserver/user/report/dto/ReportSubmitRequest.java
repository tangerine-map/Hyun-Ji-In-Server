package com.example.hyunjiinserver.user.report.dto;

import com.example.hyunjiinserver.core.report.application.SubmitReportCommand;
import com.example.hyunjiinserver.core.report.domain.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReportSubmitRequest(
        @Schema(
                description = "신고 유형입니다. 폐업/휴무 CLOSED_OR_ON_BREAK, 위치 오류 LOCATION_ERROR, 메뉴·가격 오류 MENU_PRICE_ERROR, 기타 OTHER",
                example = "MENU_PRICE_ERROR"
        )
        @NotBlank(message = "신고 유형은 필수입니다.")
        String type,

        @Schema(description = "신고 상세 내용입니다. 생략할 수 있습니다.", example = "고기국수 가격이 9,000원에서 10,000원으로 올랐습니다.")
        @Size(max = 1000, message = "신고 내용은 1000자 이하여야 합니다.")
        String content
) {

    public SubmitReportCommand toCommand(String deviceId, Long restaurantId) {
        return new SubmitReportCommand(deviceId, restaurantId, ReportType.from(type), content);
    }
}
