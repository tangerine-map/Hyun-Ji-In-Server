package com.example.hyunjiinserver.user.report.dto;

import com.example.hyunjiinserver.core.report.application.MyReportResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record MyReportResponse(
        @Schema(description = "신고 ID입니다.", example = "1")
        Long id,

        @Schema(description = "신고한 식당 ID입니다.", example = "1")
        Long restaurantId,

        @Schema(description = "신고한 식당명입니다. 식당이 삭제된 경우 null입니다.", example = "제주 고기국수 현지인집")
        String restaurantName,

        @Schema(description = "신고 유형 코드입니다.", example = "MENU_PRICE_ERROR")
        String type,

        @Schema(description = "신고 유형 표시 문구입니다.", example = "메뉴·가격 오류")
        String typeDescription,

        @Schema(description = "신고 상세 내용입니다.", example = "고기국수 가격이 9,000원에서 10,000원으로 올랐습니다.")
        String content,

        @Schema(description = "신고 처리 상태 코드입니다. RECEIVED, REVIEWING, APPLIED, REJECTED", example = "RECEIVED")
        String status,

        @Schema(description = "신고 처리 상태 표시 문구입니다. 접수, 검토, 반영, 반려", example = "접수")
        String statusDescription,

        @Schema(description = "신고 접수 시각입니다.", example = "2026-07-22T12:30:00+09:00")
        OffsetDateTime createdAt,

        @Schema(description = "상태가 마지막으로 변경된 시각입니다.", example = "2026-07-22T12:30:00+09:00")
        OffsetDateTime updatedAt
) {

    public static MyReportResponse from(MyReportResult result) {
        return new MyReportResponse(
                result.id(),
                result.restaurantId(),
                result.restaurantName(),
                result.type(),
                result.typeDescription(),
                result.content(),
                result.status(),
                result.statusDescription(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
