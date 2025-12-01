package com.hyperativa.cardapi.auth.jwt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    // Secret must be at least 32 bytes for HS256
    private static final String TEST_SECRET =
        "this-is-a-test-secret-key-32bytes-min-123";

    private final JwtTokenProvider jwtTokenProvider =
        new JwtTokenProvider(TEST_SECRET, 3600000L); // 1 hour

    @Test
    void shouldGenerateAndParseToken() {
        String username = "admin";

        String token = jwtTokenProvider.generateToken(username);

        assertThat(token).isNotBlank();

        String extracted = jwtTokenProvider.getUsernameFromToken(token);

        assertThat(extracted).isEqualTo(username);
    }

    @Test
    void shouldValidateValidToken() {
        String token = jwtTokenProvider.generateToken("user");

        boolean valid = jwtTokenProvider.validateToken(token);

        assertThat(valid).isTrue();
    }

    @Test
    void shouldRejectInvalidToken() {
        String invalid = "this-is-not-a-jwt";

        boolean valid = jwtTokenProvider.validateToken(invalid);

        assertThat(valid).isFalse();
    }
}
