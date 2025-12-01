package com.hyperativa.cardapi.card.repository;

import com.hyperativa.cardapi.card.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardHash(String cardHash);

    boolean existsByCardHash(String cardHash);
}
