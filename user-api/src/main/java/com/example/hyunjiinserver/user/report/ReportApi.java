package com.example.hyunjiinserver.user.report;

import com.example.hyunjiinserver.user.global.error.dto.ErrorResponse;
import com.example.hyunjiinserver.user.global.error.dto.ValidationErrorResponse;
import com.example.hyunjiinserver.user.report.dto.MyReportsResponse;
import com.example.hyunjiinserver.user.report.dto.ReportSubmitRequest;
import com.example.hyunjiinserver.user.report.dto.ReportSubmitResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Reports", description = "식당 정보 오류 신고 API")
@RequestMapping("/api")
public interface ReportApi {

    @Operation(
            summary = "식당 정보 오류 신고 제출",
            description = """
                    식당 상세 화면의 '정보 오류 신고'에서 호출합니다.

                    - 신고 유형은 폐업/휴무 `CLOSED_OR_ON_BREAK`, 위치 오류 `LOCATION_ERROR`, 메뉴·가격 오류 `MENU_PRICE_ERROR`, 기타 `OTHER` 중 하나를 전달합니다.
                    - 신고는 `접수` 상태로 등록되며, 접수 완료 안내 문구를 함께 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "신고 접수 성공",
                    content = @Content(
                            schema = @Schema(implementation = ReportSubmitResponse.class),
                            examples = @ExampleObject(
                                    name = "신고 접수 예시",
                                    value = """
                                            {
                                              "reportId": 1,
                                              "status": "RECEIVED",
                                              "statusDescription": "접수",
                                              "guideMessage": "신고가 접수되었습니다. 처리까지 시간이 걸릴 수 있습니다.",
                                              "createdAt": "2026-07-22T12:30:00+09:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "신고 유형이 올바르지 않거나 요청 값이 잘못됨",
                    content = @Content(
                            schema = @Schema(implementation = ValidationErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "신고 유형 오류 예시",
                                    value = """
                                            {
                                              "code": "INVALID_REPORT_TYPE",
                                              "message": "신고 유형이 올바르지 않습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "식당을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "식당 없음 예시",
                                    value = """
                                            {
                                              "code": "RESTAURANT_NOT_FOUND",
                                              "message": "식당을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/restaurants/{restaurantId}/reports")
    @ResponseStatus(HttpStatus.CREATED)
    ReportSubmitResponse submitReport(
            @Parameter(description = "기기 식별자입니다. 내 신고 내역 조회에 사용됩니다.", example = "device-1234", required = true)
            @RequestHeader("X-Device-Id") String deviceId,
            @Parameter(description = "신고할 식당 ID입니다.", example = "1", required = true)
            @PathVariable Long restaurantId,
            @Valid @RequestBody ReportSubmitRequest request
    );

    @Operation(
            summary = "내 신고 내역 조회",
            description = "마이페이지에서 내가 제출한 신고 내역을 최신순으로 조회합니다. 각 신고의 처리 상태(접수/검토/반영/반려)를 확인할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "내 신고 내역 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = MyReportsResponse.class),
                            examples = @ExampleObject(
                                    name = "내 신고 내역 예시",
                                    value = """
                                            {
                                              "reports": [
                                                {
                                                  "id": 1,
                                                  "restaurantId": 1,
                                                  "restaurantName": "제주 고기국수 현지인집",
                                                  "type": "MENU_PRICE_ERROR",
                                                  "typeDescription": "메뉴·가격 오류",
                                                  "content": "고기국수 가격이 9,000원에서 10,000원으로 올랐습니다.",
                                                  "status": "RECEIVED",
                                                  "statusDescription": "접수",
                                                  "createdAt": "2026-07-22T12:30:00+09:00",
                                                  "updatedAt": "2026-07-22T12:30:00+09:00"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/reports/me")
    MyReportsResponse getMyReports(
            @Parameter(description = "기기 식별자입니다. 이 값 기준으로 신고 내역을 조회합니다.", example = "device-1234", required = true)
            @RequestHeader("X-Device-Id") String deviceId
    );
}
