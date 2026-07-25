package com.example.hyunjiinserver.core.restaurant.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "restaurants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tour_content_id", unique = true, length = 30)
    private String tourContentId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 30)
    private String phoneNumber;

    @Column(length = 100)
    private String openingHours;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(length = 500)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RestaurantStatus status = RestaurantStatus.OPEN;

    @Column(nullable = false)
    private boolean localRecommended;

    @Column(length = 100)
    private String localRecommendationReason;

    @Column(length = 30)
    private String priceAdequacyLabel;

    @Column(length = 255)
    private String priceAdequacyDescription;

    @Column(name = "tour_modified_at")
    private OffsetDateTime tourModifiedAt;

    @Column(name = "tour_synced_at")
    private OffsetDateTime tourSyncedAt;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RestaurantMenu> menus = new ArrayList<>();

    public Optional<RestaurantMenu> representativeMenu() {
        return menus.stream()
                .filter(RestaurantMenu::isRepresentative)
                .min(Comparator.comparing(RestaurantMenu::getId));
    }

    public static Restaurant importedFromTourApi(
            String tourContentId,
            String name,
            String category,
            String address,
            String phoneNumber,
            String openingHours,
            double latitude,
            double longitude,
            String summary,
            String representativeMenuName,
            OffsetDateTime tourModifiedAt,
            OffsetDateTime syncedAt
    ) {
        Restaurant restaurant = new Restaurant();
        restaurant.tourContentId = tourContentId;
        restaurant.status = RestaurantStatus.UNKNOWN;
        restaurant.localRecommended = false;
        restaurant.updateTourApiInformation(
                name,
                category,
                address,
                phoneNumber,
                openingHours,
                latitude,
                longitude,
                summary,
                representativeMenuName,
                tourModifiedAt,
                syncedAt
        );
        return restaurant;
    }

    public void updateTourApiInformation(
            String name,
            String category,
            String address,
            String phoneNumber,
            String openingHours,
            double latitude,
            double longitude,
            String summary,
            String representativeMenuName,
            OffsetDateTime tourModifiedAt,
            OffsetDateTime syncedAt
    ) {
        this.name = name;
        this.category = category;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.openingHours = openingHours;
        this.latitude = latitude;
        this.longitude = longitude;
        this.summary = summary;
        this.tourModifiedAt = tourModifiedAt;
        this.tourSyncedAt = syncedAt;
        synchronizeRepresentativeMenu(representativeMenuName);
    }

    private void synchronizeRepresentativeMenu(String representativeMenuName) {
        if (representativeMenuName == null || representativeMenuName.isBlank()) {
            return;
        }

        Optional<RestaurantMenu> representativeMenu = menus.stream()
                .filter(RestaurantMenu::isRepresentative)
                .findFirst();
        if (representativeMenu.isPresent()) {
            representativeMenu.get().updateImportedName(representativeMenuName);
            return;
        }
        menus.add(RestaurantMenu.unpricedRepresentative(this, representativeMenuName));
    }
}
