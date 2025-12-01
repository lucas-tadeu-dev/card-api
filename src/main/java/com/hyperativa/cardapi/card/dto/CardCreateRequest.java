package com.hyperativa.cardapi.card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CardCreateRequest {

    @NotBlank
    @Size(min = 12, max = 19)
    @Pattern(regexp = "\\d+")
    private String cardNumber;

    @Size(max = 100)
    private String holderName;

    @Size(min = 2, max = 2)
    private String expirationMonth;

    @Size(min = 4, max = 4)
    private String expirationYear;
}
