package com.hyperativa.cardapi.auth.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperativa.cardapi.auth.dto.LoginRequest;
import com.hyperativa.cardapi.auth.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // disable Spring Security filters for this test
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        Authentication auth = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
        when(auth.getName()).thenReturn("admin");
        when(jwtTokenProvider.generateToken("admin")).thenReturn("fake-jwt-token");

        mockMvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("fake-jwt-token"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }
}
