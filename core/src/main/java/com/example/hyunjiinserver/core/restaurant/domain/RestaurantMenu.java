package com.example.hyunjiinserver.core.restaurant.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "restaurant_menus")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private Integer price;

    @Column(nullable = false)
    private boolean representative;

    static RestaurantMenu unpricedRepresentative(Restaurant restaurant, String name) {
        RestaurantMenu menu = new RestaurantMenu();
        menu.restaurant = restaurant;
        menu.name = name;
        menu.representative = true;
        return menu;
    }

    void updateImportedName(String name) {
        if (price == null) {
            this.name = name;
        }
    }
}
