package com.hyperativa.cardapi.auth.web;

import com.hyperativa.cardapi.auth.dto.LoginRequest;
import com.hyperativa.cardapi.auth.dto.LoginResponse;
import com.hyperativa.cardapi.auth.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication.getName());

        return ResponseEntity.ok(
            LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build()
        );
    }
}
