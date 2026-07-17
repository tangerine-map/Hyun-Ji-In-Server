package com.example.hyunjiinserver.user.search;

import com.example.hyunjiinserver.user.global.error.ErrorResponse;
import com.example.hyunjiinserver.user.global.error.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Search", description = "검색 API")
@RequestMapping("/api/search")
public interface SearchApi {

    @Operation(
            summary = "검색어 자동완성 조회",
            description = """
                    검색창 입력 중 노출할 자동완성 추천어를 조회합니다.

                    - 현재는 식당명과 메뉴명 기준 추천어를 제공합니다.
                    - 추천어 선택 시 `keyword` 값을 지도 식당 조회 API의 `keyword`로 전달하면 됩니다.
                    - 지역명/관광지명 추천은 관련 데이터 도메인 추가 후 같은 응답 형식으로 확장합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "검색어 자동완성 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = SearchSuggestionsResponse.class),
                            examples = @ExampleObject(
                                    name = "검색어 자동완성 예시",
                                    value = """
                                            {
                                              "suggestions": [
                                                {
                                                  "type": "MENU",
                                                  "keyword": "고기국수",
                                                  "referenceId": null,
                                                  "description": "메뉴"
                                                },
                                                {
                                                  "type": "RESTAURANT",
                                                  "keyword": "고기국수 현지인집",
                                                  "referenceId": 1,
                                                  "description": "음식점"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 파라미터가 올바르지 않음",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/suggestions")
    SearchSuggestionsResponse getSuggestions(
            @Valid @ParameterObject @ModelAttribute SearchSuggestionRequest request
    );
}
