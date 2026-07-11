package com.example.hyunjiinserver.core.restaurant.infrastructure;

import com.example.hyunjiinserver.core.restaurant.domain.LocalComment;
import com.example.hyunjiinserver.core.restaurant.domain.Restaurant;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantCommentSearchCondition;
import com.example.hyunjiinserver.core.restaurant.domain.RestaurantMapSearchCondition;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class RestaurantQueryRepository {

    private final EntityManager entityManager;

    public RestaurantQueryRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Restaurant> findByMapBounds(RestaurantMapSearchCondition condition) {
        String jpql = """
                select distinct r
                from Restaurant r
                left join fetch r.menus m
                where r.latitude between :southWestLatitude and :northEastLatitude
                  and r.longitude between :southWestLongitude and :northEastLongitude
                  and (:keyword is null or r.name like concat('%', :keyword, '%') or m.name like concat('%', :keyword, '%'))
                  and (:category is null or r.category = :category)
                  and (:localRecommended is null or r.localRecommended = :localRecommended)
                order by r.localRecommended desc, r.id asc
                """;

        return entityManager.createQuery(jpql, Restaurant.class)
                .setParameter("southWestLatitude", condition.southWestLatitude())
                .setParameter("northEastLatitude", condition.northEastLatitude())
                .setParameter("southWestLongitude", condition.southWestLongitude())
                .setParameter("northEastLongitude", condition.northEastLongitude())
                .setParameter("keyword", blankToNull(condition.keyword()))
                .setParameter("category", blankToNull(condition.category()))
                .setParameter("localRecommended", condition.localRecommended())
                .setMaxResults(condition.limit())
                .getResultList();
    }

    public List<LocalComment> findComments(RestaurantCommentSearchCondition condition) {
        String orderBy = condition.sortByHelpful()
                ? "order by c.helpfulCount desc, c.createdAt desc"
                : "order by c.createdAt desc";

        String jpql = """
                select c
                from LocalComment c
                where c.restaurant.id = :restaurantId
                %s
                """.formatted(orderBy);

        return entityManager.createQuery(jpql, LocalComment.class)
                .setParameter("restaurantId", condition.restaurantId())
                .setMaxResults(condition.limit())
                .getResultList();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
