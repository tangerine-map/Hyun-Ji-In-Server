package com.example.hyunjiinserver.core.report.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "restaurant_reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, length = 64)
    private String deviceId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportType type;

    @Column(length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public RestaurantReport(String deviceId, Long restaurantId, ReportType type, String content) {
        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Asia/Seoul"));
        this.deviceId = deviceId;
        this.restaurantId = restaurantId;
        this.type = type;
        this.content = content;
        this.status = ReportStatus.RECEIVED;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void changeStatus(ReportStatus status) {
        this.status = status;
        this.updatedAt = OffsetDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
