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

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RestaurantMenu> menus = new ArrayList<>();

    public Optional<RestaurantMenu> representativeMenu() {
        return menus.stream()
                .filter(RestaurantMenu::isRepresentative)
                .min(Comparator.comparing(RestaurantMenu::getId));
    }
}
