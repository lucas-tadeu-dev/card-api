package com.hyperativa.cardapi.card.service;

import com.hyperativa.cardapi.card.domain.Card;
import com.hyperativa.cardapi.card.dto.CardCheckRequest;
import com.hyperativa.cardapi.card.dto.CardCheckResponse;
import com.hyperativa.cardapi.card.dto.CardCreateRequest;
import com.hyperativa.cardapi.card.dto.CardResponse;
import com.hyperativa.cardapi.card.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // allow some unused stubbings
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardHashService cardHashService;

    @InjectMocks
    private CardService cardService;

    private static final String CARD_NUMBER = "4111111111111111";
    private static final String CARD_HASH = "hash-123";
    private static final String MASKED = "************1111";

    @BeforeEach
    void setupMocks() {
        when(cardHashService.hashCardNumber(CARD_NUMBER)).thenReturn(CARD_HASH);
        when(cardHashService.maskCardNumber(CARD_NUMBER)).thenReturn(MASKED);
    }

    @Test
    void createCardShouldSaveNewCardWhenNotExists() {
        when(cardRepository.existsByCardHash(CARD_HASH)).thenReturn(false);

        Card saved = Card.builder()
            .id(1L)
            .cardHash(CARD_HASH)
            .maskedCardNumber(MASKED)
            .createdAt(Instant.now())
            .build();

        when(cardRepository.save(any(Card.class))).thenReturn(saved);

        CardCreateRequest request = new CardCreateRequest();
        request.setCardNumber(CARD_NUMBER);
        request.setHolderName("John Doe");
        request.setExpirationMonth("12");
        request.setExpirationYear("2030");

        CardResponse response = cardService.createCard(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getMaskedCardNumber()).isEqualTo(MASKED);

        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCardShouldReturnExistingWhenHashAlreadyPresent() {
        Card existing = Card.builder()
            .id(5L)
            .cardHash(CARD_HASH)
            .maskedCardNumber(MASKED)
            .createdAt(Instant.now())
            .build();

        when(cardRepository.existsByCardHash(CARD_HASH)).thenReturn(true);
        when(cardRepository.findByCardHash(CARD_HASH)).thenReturn(Optional.of(existing));

        CardCreateRequest request = new CardCreateRequest();
        request.setCardNumber(CARD_NUMBER);

        CardResponse response = cardService.createCard(request);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getMaskedCardNumber()).isEqualTo(MASKED);

        verify(cardRepository, never()).save(any());
    }

    @Test
    void checkCardShouldReturnExistsTrueWhenCardFound() {
        Card existing = Card.builder()
            .id(10L)
            .cardHash(CARD_HASH)
            .maskedCardNumber(MASKED)
            .createdAt(Instant.now())
            .build();

        when(cardRepository.findByCardHash(CARD_HASH)).thenReturn(Optional.of(existing));

        CardCheckRequest request = new CardCheckRequest();
        request.setCardNumber(CARD_NUMBER);

        CardCheckResponse response = cardService.checkCard(request);

        assertThat(response.isExists()).isTrue();
        assertThat(response.getCardId()).isEqualTo(10L);
    }

    @Test
    void checkCardShouldReturnExistsFalseWhenNotFound() {
        when(cardRepository.findByCardHash(CARD_HASH)).thenReturn(Optional.empty());

        CardCheckRequest request = new CardCheckRequest();
        request.setCardNumber(CARD_NUMBER);

        CardCheckResponse response = cardService.checkCard(request);

        assertThat(response.isExists()).isFalse();
        assertThat(response.getCardId()).isNull();
    }

    @Test
    void importFromTxtShouldImportEachLineAsCard() {
        String content = CARD_NUMBER + "\n" + "5555444433331111\n";
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "cards.txt",
            "text/plain",
            content.getBytes()
        );

        when(cardHashService.hashCardNumber(anyString())).thenReturn("hash-any");
        when(cardHashService.maskCardNumber(anyString())).thenReturn("************1111");
        when(cardRepository.existsByCardHash(anyString())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        int imported = cardService.importFromTxt(file);

        assertThat(imported).isEqualTo(2);
        verify(cardRepository, times(2)).save(any(Card.class));
    }
}
