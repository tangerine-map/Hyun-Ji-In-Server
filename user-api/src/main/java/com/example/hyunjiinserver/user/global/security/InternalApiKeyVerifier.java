package com.example.hyunjiinserver.user.global.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class InternalApiKeyVerifier {

    private final String expectedKey;

    public InternalApiKeyVerifier(@Value("${tour-api.sync-api-key:}") String expectedKey) {
        this.expectedKey = expectedKey;
    }

    public void verify(String providedKey) {
        if (expectedKey == null || expectedKey.isBlank()) {
            throw new InternalApiAuthenticationException(
                    "SYNC_API_KEY_NOT_CONFIGURED",
                    "동기화 API 인증키가 설정되지 않았습니다.",
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
        if (providedKey == null || providedKey.isBlank() || !matches(providedKey)) {
            throw new InternalApiAuthenticationException(
                    "INVALID_SYNC_API_KEY",
                    "동기화 API 인증키가 올바르지 않습니다.",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    private boolean matches(String providedKey) {
        return MessageDigest.isEqual(
                expectedKey.getBytes(StandardCharsets.UTF_8),
                providedKey.getBytes(StandardCharsets.UTF_8)
        );
    }
}
