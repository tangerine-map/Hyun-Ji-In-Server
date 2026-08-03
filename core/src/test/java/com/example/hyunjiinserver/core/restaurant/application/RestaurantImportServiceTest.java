package com.example.hyunjiinserver.core.restaurant.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.hyunjiinserver.core.restaurant.domain.LocalComment;
import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantCommentSearchCondition;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantMapSearchCondition;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantRepository;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

class RestaurantImportServiceTest {

    private final InMemoryRestaurantRepository repository = new InMemoryRestaurantRepository();
    private final Clock clock = Clock.fixed(Instant.parse("2026-07-24T03:00:00Z"), ZoneOffset.ofHours(9));
    private final RestaurantImportTransactionService transactionService =
            new RestaurantImportTransactionService(repository);
    private final RestaurantImportService service = new RestaurantImportService(transactionService, clock);

    @Test
    void savesEachRestaurantInRequiresNewTransaction() throws NoSuchMethodException {
        Transactional transactional = RestaurantImportTransactionService.class
                .getMethod("upsertOne", TourApiRestaurantData.class, OffsetDateTime.class)
                .getAnnotation(Transactional.class);

        assertEquals(Propagation.REQUIRES_NEW, transactional.propagation());
    }

    @Test
    void createsThenUpdatesRestaurantByTourContentId() {
        TourApiRestaurantData first = source("제주식당", "한식", "고기국수");

        RestaurantImportResult created = service.upsertTourApiRestaurants(List.of(first));
        Restaurant restaurant = repository.findByTourContentId("3012345").orElseThrow();

        assertEquals(1, created.createdCount());
        assertEquals(0, created.failedCount());
        assertEquals(RestaurantStatus.UNKNOWN, restaurant.getStatus());
        assertEquals("제주식당", restaurant.getName());
        assertEquals("고기국수", restaurant.representativeMenu().orElseThrow().getName());
        assertNull(restaurant.representativeMenu().orElseThrow().getPrice());
        assertEquals(OffsetDateTime.parse("2026-07-24T12:00:00+09:00"), restaurant.getTourSyncedAt());

        RestaurantImportResult updated = service.upsertTourApiRestaurants(
                List.of(source("제주식당 새 이름", "음식점", "비빔국수"))
        );

        assertEquals(0, updated.createdCount());
        assertEquals(1, updated.updatedCount());
        assertEquals("제주식당 새 이름", restaurant.getName());
        assertEquals("비빔국수", restaurant.representativeMenu().orElseThrow().getName());
    }

    @Test
    void continuesWithNextRestaurantWhenOneSaveFails() {
        RestaurantImportTransactionService failingTransactionService =
                new RestaurantImportTransactionService(repository) {
                    @Override
                    public RestaurantImportItemResult upsertOne(
                            TourApiRestaurantData source,
                            OffsetDateTime syncedAt
                    ) {
                        if (source.contentId().equals("failed-content")) {
                            throw new IllegalStateException("save failed");
                        }
                        return super.upsertOne(source, syncedAt);
                    }
                };
        RestaurantImportService importService = new RestaurantImportService(failingTransactionService, clock);

        RestaurantImportResult result = importService.upsertTourApiRestaurants(List.of(
                source("first-content", "첫 번째 식당", "한식", "고기국수"),
                source("failed-content", "실패 식당", "한식", "비빔국수"),
                source("last-content", "마지막 식당", "한식", "잔치국수")
        ));

        assertEquals(3, result.fetchedCount());
        assertEquals(2, result.createdCount());
        assertEquals(0, result.updatedCount());
        assertEquals(1, result.failedCount());
        assertEquals("첫 번째 식당", repository.findByTourContentId("first-content").orElseThrow().getName());
        assertEquals("마지막 식당", repository.findByTourContentId("last-content").orElseThrow().getName());
    }

    private TourApiRestaurantData source(String name, String category, String menu) {
        return source("3012345", name, category, menu);
    }

    private TourApiRestaurantData source(String contentId, String name, String category, String menu) {
        return new TourApiRestaurantData(
                contentId,
                name,
                category,
                "제주특별자치도 제주시 테스트로 1",
                "064-123-4567",
                "매일 09:00~18:00",
                33.4996,
                126.5312,
                "제주 식당 소개",
                menu,
                OffsetDateTime.parse("2026-07-24T11:00:00+09:00")
        );
    }

    private static class InMemoryRestaurantRepository implements RestaurantRepository {

        private final Map<String, Restaurant> restaurants = new LinkedHashMap<>();

        @Override
        public List<Restaurant> findByMapBounds(RestaurantMapSearchCondition condition) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Restaurant> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public Optional<Restaurant> findByIdForUpdate(Long id) {
            return Optional.empty();
        }

        @Override
        public Optional<Restaurant> findByTourContentId(String tourContentId) {
            return Optional.ofNullable(restaurants.get(tourContentId));
        }

        @Override
        public List<Restaurant> findByIds(Collection<Long> ids) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<LocalComment> findComments(RestaurantCommentSearchCondition condition) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Restaurant save(Restaurant restaurant) {
            restaurants.put(restaurant.getTourContentId(), restaurant);
            return restaurant;
        }
    }
}
