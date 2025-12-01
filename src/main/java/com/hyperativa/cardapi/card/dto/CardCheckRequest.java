package com.hyperativa.cardapi.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CardCheckRequest {

    @NotBlank
    @Size(min = 12, max = 19)
    @Pattern(regexp = "\\d+")
    private String cardNumber;
}
