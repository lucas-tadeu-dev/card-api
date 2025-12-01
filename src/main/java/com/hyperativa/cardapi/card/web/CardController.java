package com.hyperativa.cardapi.card.web;

import com.hyperativa.cardapi.card.dto.*;
import com.hyperativa.cardapi.card.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardCreateRequest request) {
        CardResponse response = cardService.createCard(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check")
    public ResponseEntity<CardCheckResponse> checkCard(@Valid @RequestBody CardCheckRequest request) {
        CardCheckResponse response = cardService.checkCard(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadCards(@RequestParam("file") MultipartFile file) {
        int imported = cardService.importFromTxt(file);
        return ResponseEntity.ok("Imported " + imported + " cards");
    }
}
