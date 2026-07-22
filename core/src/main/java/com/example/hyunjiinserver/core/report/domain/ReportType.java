package com.example.hyunjiinserver.core.report.domain;

import com.example.hyunjiinserver.core.global.error.BusinessException;
import java.util.Arrays;

public enum ReportType {

    CLOSED_OR_ON_BREAK("폐업/휴무"),
    LOCATION_ERROR("위치 오류"),
    MENU_PRICE_ERROR("메뉴·가격 오류"),
    OTHER("기타");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public static ReportType from(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ReportErrorCode.INVALID_REPORT_TYPE));
    }
}
