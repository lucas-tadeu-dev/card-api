package com.hyperativa.cardapi.card.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardResponse {

    private Long id;
    private String maskedCardNumber;
}
