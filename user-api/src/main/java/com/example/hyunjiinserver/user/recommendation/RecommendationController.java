package com.example.hyunjiinserver.user.recommendation;

import com.example.hyunjiinserver.core.recommendation.application.RecommendationService;
import com.example.hyunjiinserver.user.recommendation.dto.RecommendRequest;
import com.example.hyunjiinserver.user.recommendation.dto.RecommendationFeedbackRequest;
import com.example.hyunjiinserver.user.recommendation.dto.RecommendationQuestionsResponse;
import com.example.hyunjiinserver.user.recommendation.dto.RecommendationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecommendationController implements RecommendationApi {

    private final RecommendationService recommendationService;

    @Override
    public RecommendationQuestionsResponse getQuestions() {
        return RecommendationQuestionsResponse.from(recommendationService.getQuestions());
    }

    @Override
    public RecommendationsResponse recommend(String deviceId, RecommendRequest request) {
        return RecommendationsResponse.from(recommendationService.recommend(request.toCommand(deviceId)));
    }

    @Override
    public RecommendationsResponse refresh(String deviceId, String sessionId) {
        return RecommendationsResponse.from(recommendationService.refresh(deviceId, sessionId));
    }

    @Override
    public void submitFeedback(String deviceId, String sessionId, RecommendationFeedbackRequest request) {
        recommendationService.submitFeedback(request.toCommand(deviceId, sessionId));
    }
}
