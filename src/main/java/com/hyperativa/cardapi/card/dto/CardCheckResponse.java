package com.hyperativa.cardapi.card.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardCheckResponse {

    private boolean exists;
    private Long cardId;
}
