package com.example.hyunjiinserver.user.restaurant;

import com.example.hyunjiinserver.core.restaurant.application.RestaurantMapResult;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantQueryService;
import com.example.hyunjiinserver.core.restaurant.application.GetRestaurantDetailQuery;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantCommentsResult;
import com.example.hyunjiinserver.core.restaurant.application.RestaurantDetailResult;
import com.example.hyunjiinserver.user.restaurant.dto.RestaurantCommentSearchRequest;
import com.example.hyunjiinserver.user.restaurant.dto.RestaurantCommentsResponse;
import com.example.hyunjiinserver.user.restaurant.dto.RestaurantDetailResponse;
import com.example.hyunjiinserver.user.restaurant.dto.RestaurantMapResponse;
import com.example.hyunjiinserver.user.restaurant.dto.RestaurantMapSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RestaurantController implements RestaurantApi {

    private final RestaurantQueryService restaurantQueryService;

    @Override
    public RestaurantMapResponse findRestaurants(RestaurantMapSearchRequest request) {
        RestaurantMapResult result = restaurantQueryService.findRestaurants(request.toQuery());
        return RestaurantMapResponse.from(result);
    }

    @Override
    public RestaurantDetailResponse getRestaurantDetail(Long restaurantId) {
        RestaurantDetailResult result = restaurantQueryService.getRestaurantDetail(
                new GetRestaurantDetailQuery(restaurantId, null)
        );
        return RestaurantDetailResponse.from(result);
    }

    @Override
    public RestaurantCommentsResponse getRestaurantComments(
            Long restaurantId,
            RestaurantCommentSearchRequest request
    ) {
        RestaurantCommentsResult result = restaurantQueryService.getRestaurantComments(request.toQuery(restaurantId));
        return RestaurantCommentsResponse.from(result);
    }
}
