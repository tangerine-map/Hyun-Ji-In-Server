package com.example.hyunjiinserver.core.recommendation.application;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    private static final RecommendationQuestionsResult QUESTIONS = new RecommendationQuestionsResult(List.of(
            new RecommendationQuestionResult(
                    "companion", "누구와 함께 식사하시나요?", false, true,
                    List.of(
                            new RecommendationQuestionOptionResult("alone", "혼자"),
                            new RecommendationQuestionOptionResult("couple", "연인"),
                            new RecommendationQuestionOptionResult("friends", "친구"),
                            new RecommendationQuestionOptionResult("family", "가족")
                    )
            ),
            new RecommendationQuestionResult(
                    "mood", "어떤 분위기를 원하시나요?", true, true,
                    List.of(
                            new RecommendationQuestionOptionResult("quiet", "조용한"),
                            new RecommendationQuestionOptionResult("local", "로컬 느낌"),
                            new RecommendationQuestionOptionResult("clean", "깔끔한"),
                            new RecommendationQuestionOptionResult("lively", "활기찬")
                    )
            ),
            new RecommendationQuestionResult(
                    "price", "가격대는 어느 정도가 좋으신가요?", false, true,
                    List.of(
                            new RecommendationQuestionOptionResult("cheap", "저렴한"),
                            new RecommendationQuestionOptionResult("normal", "보통"),
                            new RecommendationQuestionOptionResult("any", "상관없음")
                    )
            ),
            new RecommendationQuestionResult(
                    "spiciness", "매운 음식은 어떠세요?", false, true,
                    List.of(
                            new RecommendationQuestionOptionResult("no", "잘 못 먹어요"),
                            new RecommendationQuestionOptionResult("normal", "보통"),
                            new RecommendationQuestionOptionResult("love", "좋아해요")
                    )
            ),
            new RecommendationQuestionResult(
                    "menu", "오늘 먹고 싶은 메뉴가 있나요?", true, true,
                    List.of(
                            new RecommendationQuestionOptionResult("noodle", "고기국수"),
                            new RecommendationQuestionOptionResult("pork", "흑돼지"),
                            new RecommendationQuestionOptionResult("seafood", "해산물"),
                            new RecommendationQuestionOptionResult("cafe", "카페·디저트"),
                            new RecommendationQuestionOptionResult("any", "상관없음")
                    )
            ),
            new RecommendationQuestionResult(
                    "distance", "얼마나 멀리까지 이동할 수 있나요?", false, true,
                    List.of(
                            new RecommendationQuestionOptionResult("walk", "걸어서 갈 수 있는 곳"),
                            new RecommendationQuestionOptionResult("drive10", "차로 10분 이내"),
                            new RecommendationQuestionOptionResult("any", "상관없음")
                    )
            )
    ));

    public RecommendationQuestionsResult getQuestions() {
        return QUESTIONS;
    }

    public RecommendationsResult recommend(RecommendCommand command) {
        // TODO: 추천 엔진 구현 (엔진 방식 미정: 규칙 기반 vs LLM). 확정 전까지 빈 결과를 반환한다.
        return new RecommendationsResult(UUID.randomUUID().toString(), List.of());
    }

    public RecommendationsResult refresh(String deviceId, String sessionId) {
        // TODO: 추천 엔진 구현 후 세션 조건 기반 재추천을 제공한다.
        return new RecommendationsResult(sessionId, List.of());
    }

    public void submitFeedback(SubmitRecommendationFeedbackCommand command) {
        // TODO: 추천 엔진 구현 후 피드백을 저장하고 다음 추천에 반영한다.
    }
}
