package com.example.hyunjiinserver.core.restaurant.domain;

import com.example.hyunjiinserver.core.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum RestaurantErrorCode implements ErrorCode {

    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "식당을 찾을 수 없습니다."),
    INVALID_MAP_BOUNDS(HttpStatus.BAD_REQUEST, "지도 검색 영역 값이 올바르지 않습니다."),
    TOUR_API_SYNC_IN_PROGRESS(HttpStatus.CONFLICT, "이미 TourAPI 음식점 동기화가 진행 중입니다."),
    TOUR_API_SYNC_JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "TourAPI 음식점 동기화 작업을 찾을 수 없습니다."),
    TOUR_API_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "한국관광공사 음식점 정보를 가져오지 못했습니다."),
    ENRICHMENT_JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "식당 정보 보강 작업을 찾을 수 없습니다."),
    ENRICHMENT_JOB_NOT_COMPLETED(HttpStatus.CONFLICT, "식당 정보 보강 작업이 아직 완료되지 않았습니다."),
    ENRICHMENT_CANDIDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "식당 정보 보강 후보를 찾을 수 없습니다."),
    ENRICHMENT_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "식당 정보 보강 처리에 실패했습니다.");

    private final HttpStatus status;
    private final String message;

    RestaurantErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String code() {
        return name();
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}
