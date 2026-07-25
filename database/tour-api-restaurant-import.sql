ALTER TABLE restaurants
    ADD COLUMN tour_content_id VARCHAR(30) NULL,
    ADD COLUMN tour_modified_at DATETIME(6) NULL,
    ADD COLUMN tour_synced_at DATETIME(6) NULL;

CREATE UNIQUE INDEX uk_restaurants_tour_content_id
    ON restaurants (tour_content_id);

ALTER TABLE restaurant_menus
    MODIFY COLUMN price INT NULL;
