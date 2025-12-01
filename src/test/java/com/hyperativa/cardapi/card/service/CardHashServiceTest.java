package com.hyperativa.cardapi.card.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CardHashServiceTest {

    private final CardHashService cardHashService =
        new CardHashService("test-pepper");

    @Test
    void shouldGenerateDeterministicHashForSameCardNumber() {
        String cardNumber = "4111111111111111";

        String hash1 = cardHashService.hashCardNumber(cardNumber);
        String hash2 = cardHashService.hashCardNumber(cardNumber);

        assertThat(hash1).isNotBlank();
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void shouldGenerateDifferentHashForDifferentCardNumbers() {
        String hash1 = cardHashService.hashCardNumber("4111111111111111");
        String hash2 = cardHashService.hashCardNumber("5555444433331111");

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void shouldMaskCardNumberWithLast4Digits() {
        String masked = cardHashService.maskCardNumber("4111111111111111");

        assertThat(masked).isEqualTo("************1111");
    }

    @Test
    void shouldReturnFallbackMaskForShortCardNumber() {
        String masked = cardHashService.maskCardNumber("123");

        assertThat(masked).isEqualTo("****");
    }
}
