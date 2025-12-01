package com.hyperativa.cardapi.card.service;

import com.hyperativa.cardapi.card.domain.Card;
import com.hyperativa.cardapi.card.dto.*;
import com.hyperativa.cardapi.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository cardRepository;
    private final CardHashService cardHashService;

    public CardResponse createCard(CardCreateRequest request) {
        String hash = cardHashService.hashCardNumber(request.getCardNumber());
        String masked = cardHashService.maskCardNumber(request.getCardNumber());

        if (cardRepository.existsByCardHash(hash)) {
            Card existing = cardRepository.findByCardHash(hash).orElseThrow();
            log.info("Card already exists: id={} masked={}", existing.getId(), existing.getMaskedCardNumber());
            return CardResponse.builder()
                .id(existing.getId())
                .maskedCardNumber(existing.getMaskedCardNumber())
                .build();
        }

        Card card = Card.builder()
            .cardHash(hash)
            .maskedCardNumber(masked)
            .holderName(request.getHolderName())
            .expirationMonth(request.getExpirationMonth())
            .expirationYear(request.getExpirationYear())
            .createdAt(Instant.now())
            .build();

        card = cardRepository.save(card);
        log.info("Created card id={} masked={}", card.getId(), card.getMaskedCardNumber());

        return CardResponse.builder()
            .id(card.getId())
            .maskedCardNumber(card.getMaskedCardNumber())
            .build();
    }

    public CardCheckResponse checkCard(CardCheckRequest request) {
        String hash = cardHashService.hashCardNumber(request.getCardNumber());

        return cardRepository.findByCardHash(hash)
            .map(card -> CardCheckResponse.builder()
                .exists(true)
                .cardId(card.getId())
                .build())
            .orElseGet(() -> CardCheckResponse.builder()
                .exists(false)
                .cardId(null)
                .build());
    }

    public int importFromTxt(MultipartFile file) {
        int imported = 0;
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String cardNumber = line.trim();
                if (cardNumber.isEmpty()) {
                    continue;
                }

                CardCreateRequest req = new CardCreateRequest();
                req.setCardNumber(cardNumber);
                createCard(req);
                imported++;
            }

            log.info("Imported {} cards from file {}", imported, file.getOriginalFilename());
            return imported;
        } catch (Exception ex) {
            log.error("Failed to import cards from file {}", file.getOriginalFilename(), ex);
            throw new IllegalArgumentException("Could not import file", ex);
        }
    }
}
