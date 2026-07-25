package com.example.hyunjiinserver.user.global.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class InternalApiKeyVerifierTest {

    @Test
    void acceptsMatchingKey() {
        InternalApiKeyVerifier verifier = new InternalApiKeyVerifier("sync-secret");

        assertDoesNotThrow(() -> verifier.verify("sync-secret"));
    }

    @Test
    void rejectsMissingOrDifferentKey() {
        InternalApiKeyVerifier verifier = new InternalApiKeyVerifier("sync-secret");

        InternalApiAuthenticationException missing = assertThrows(
                InternalApiAuthenticationException.class,
                () -> verifier.verify(null)
        );
        InternalApiAuthenticationException different = assertThrows(
                InternalApiAuthenticationException.class,
                () -> verifier.verify("different")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, missing.getStatus());
        assertEquals("INVALID_SYNC_API_KEY", different.getCode());
    }

    @Test
    void rejectsAllRequestsWhenServerKeyIsNotConfigured() {
        InternalApiKeyVerifier verifier = new InternalApiKeyVerifier("");

        InternalApiAuthenticationException exception = assertThrows(
                InternalApiAuthenticationException.class,
                () -> verifier.verify("anything")
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
        assertEquals("SYNC_API_KEY_NOT_CONFIGURED", exception.getCode());
    }
}
