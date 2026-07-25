package com.example.hyunjiinserver.user.global.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SwaggerUiLabelTransformerTest {

    @Test
    void appendsLabelsThatDistinguishExamplesFromLiveResponses() {
        String css = SwaggerUiLabelTransformer.appendResponseLabelStyles("body {}\n");

        assertTrue(css.contains("응답 종류 및 문서 예시"));
        assertTrue(css.contains("실제 서버 응답"));
        assertTrue(css.startsWith("body {}"));
    }
}
