package com.hyperativa.cardapi.card.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperativa.cardapi.card.dto.*;
import com.hyperativa.cardapi.card.service.CardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CardController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filters for unit test
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCardShouldReturnResponse() throws Exception {
        CardCreateRequest req = new CardCreateRequest();
        req.setCardNumber("4111111111111111");
        req.setHolderName("John Doe");
        req.setExpirationMonth("12");
        req.setExpirationYear("2030");

        CardResponse resp = CardResponse.builder()
            .id(1L)
            .maskedCardNumber("************1111")
            .build();

        when(cardService.createCard(any(CardCreateRequest.class))).thenReturn(resp);

        mockMvc.perform(
                post("/api/cards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.maskedCardNumber").value("************1111"));
    }

    @Test
    void checkCardShouldReturnExistsFlag() throws Exception {
        CardCheckRequest req = new CardCheckRequest();
        req.setCardNumber("4111111111111111");

        CardCheckResponse resp = CardCheckResponse.builder()
            .exists(true)
            .cardId(1L)
            .build();

        when(cardService.checkCard(any(CardCheckRequest.class))).thenReturn(resp);

        mockMvc.perform(
                post("/api/cards/check")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.exists").value(true))
            .andExpect(jsonPath("$.cardId").value(1L));
    }

    @Test
    void uploadCardsShouldReturnImportedCount() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "cards.txt",
            "text/plain",
            "4111111111111111\n".getBytes()
        );

        when(cardService.importFromTxt(any())).thenReturn(1);

        mockMvc.perform(
                multipart("/api/cards/upload")
                    .file(file)
            )
            .andExpect(status().isOk())
            .andExpect(content().string("Imported 1 cards"));
    }
}
