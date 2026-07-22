package com.example.hyunjiinserver.core.useractivity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "saved_restaurants",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_saved_restaurants_device_restaurant",
                columnNames = {"device_id", "restaurant_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, length = 64)
    private String deviceId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(nullable = false)
    private OffsetDateTime savedAt;

    public SavedRestaurant(String deviceId, Long restaurantId) {
        this.deviceId = deviceId;
        this.restaurantId = restaurantId;
        this.savedAt = OffsetDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
