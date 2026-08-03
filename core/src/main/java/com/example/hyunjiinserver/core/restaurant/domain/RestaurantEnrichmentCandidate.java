package com.example.hyunjiinserver.core.restaurant.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "restaurant_enrichment_candidates")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantEnrichmentCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id", nullable = false, length = 36)
    private String jobId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_name", nullable = false, length = 30)
    private RestaurantEnrichmentField field;

    @Column(name = "value_text", length = 1000)
    private String valueText;

    @Column(name = "value_number")
    private Integer valueNumber;

    @Column(nullable = false)
    private boolean representative;

    @Column(name = "source_url", nullable = false, length = 1000)
    private String sourceUrl;

    @Column(nullable = false, length = 1000)
    private String evidence;

    @Column(nullable = false)
    private double confidence;

    @Column(nullable = false)
    private boolean accepted;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public static RestaurantEnrichmentCandidate create(
            UUID jobId,
            Long restaurantId,
            RestaurantEnrichmentField field,
            String valueText,
            Integer valueNumber,
            boolean representative,
            String sourceUrl,
            String evidence,
            double confidence,
            OffsetDateTime createdAt
    ) {
        RestaurantEnrichmentCandidate candidate = new RestaurantEnrichmentCandidate();
        candidate.jobId = jobId.toString();
        candidate.restaurantId = restaurantId;
        candidate.field = field;
        candidate.valueText = truncate(valueText, 1000);
        candidate.valueNumber = valueNumber;
        candidate.representative = representative;
        candidate.sourceUrl = truncate(sourceUrl, 1000);
        candidate.evidence = truncate(evidence, 1000);
        candidate.confidence = Math.max(0, Math.min(confidence, 1));
        candidate.createdAt = createdAt;
        return candidate;
    }

    public UUID jobIdAsUuid() {
        return UUID.fromString(jobId);
    }

    public void accept() {
        accepted = true;
    }

    private static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
