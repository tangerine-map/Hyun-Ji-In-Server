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

    public boolean applyPhoneNumberIfMissing(String candidate) {
        if (!isBlank(phoneNumber) || isBlank(candidate)) {
            return false;
        }
        phoneNumber = candidate.trim();
        return true;
    }

    public boolean applyOpeningHoursIfMissing(String candidate) {
        if (!isMissingOpeningHours() || isBlank(candidate)) {
            return false;
        }
        openingHours = candidate.trim();
        return true;
    }

    public boolean applySummaryIfMissing(String candidate) {
        if (!isBlank(summary) || isBlank(candidate)) {
            return false;
        }
        summary = candidate.trim();
        return true;
    }

    public boolean applyStatusIfUnknown(RestaurantStatus candidate) {
        if (status != RestaurantStatus.UNKNOWN || candidate == null || candidate == RestaurantStatus.UNKNOWN) {
            return false;
        }
        status = candidate;
        return true;
    }

    public boolean applyMenuIfMissing(String menuName, Integer price, boolean representative) {
        if (isBlank(menuName)) {
            return false;
        }

        Optional<RestaurantMenu> existing = menus.stream()
                .filter(menu -> menu.hasSameName(menuName))
                .findFirst();
        if (existing.isPresent()) {
            return existing.get().applyPriceIfMissing(price);
        }

        boolean shouldBeRepresentative = representative && representativeMenu().isEmpty();
        menus.add(RestaurantMenu.discovered(this, menuName.trim(), price, shouldBeRepresentative));
        return true;
    }

    public boolean hasMissingPhoneNumber() {
        return isBlank(phoneNumber);
    }

    public boolean hasMissingOpeningHours() {
        return isMissingOpeningHours();
    }

    public boolean hasMissingSummary() {
        return isBlank(summary);
    }

    public boolean hasUnknownStatus() {
        return status == RestaurantStatus.UNKNOWN;
    }

    public boolean hasNoMenus() {
        return menus.isEmpty();
    }

    public boolean hasUnpricedMenus() {
        return menus.stream().anyMatch(menu -> menu.getPrice() == null);
    }

    private boolean isMissingOpeningHours() {
        return isBlank(openingHours) || "-".equals(openingHours.trim());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
