package com.hyperativa.cardapi.card.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
    name = "cards",
    indexes = {
        @Index(name = "idx_cards_card_hash", columnList = "cardHash", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Hash of the full card number (PAN + pepper).
     */
    @Column(nullable = false, unique = true, length = 128)
    private String cardHash;

    /**
     * Masked card number, e.g. ************1234.
     */
    @Column(nullable = false, length = 32)
    private String maskedCardNumber;

    @Column(length = 100)
    private String holderName;

    @Column(length = 2)
    private String expirationMonth;

    @Column(length = 4)
    private String expirationYear;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
