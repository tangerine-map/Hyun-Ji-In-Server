CREATE TABLE IF NOT EXISTS restaurant_enrichment_candidates (
    id BIGINT NOT NULL AUTO_INCREMENT,
    job_id VARCHAR(36) NOT NULL,
    restaurant_id BIGINT NOT NULL,
    field_name VARCHAR(30) NOT NULL,
    value_text VARCHAR(1000) NULL,
    value_number INT NULL,
    representative BOOLEAN NOT NULL DEFAULT FALSE,
    source_url VARCHAR(1000) NOT NULL,
    evidence VARCHAR(1000) NOT NULL,
    confidence DOUBLE NOT NULL,
    accepted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_restaurant_enrichment_candidates_job_id (job_id),
    INDEX idx_restaurant_enrichment_candidates_restaurant_id (restaurant_id),
    CONSTRAINT fk_restaurant_enrichment_candidates_restaurant
        FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
);
