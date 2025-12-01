package com.hyperativa.cardapi.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.*;

import java.io.IOException;

import static org.mockito.Mockito.*;

class ApiRequestLoggingFilterTest {

    private final ApiRequestLoggingFilter filter = new ApiRequestLoggingFilter();

    @Test
    void shouldDelegateToNextFilterAndNotThrow() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/cards");
        request.setMethod("POST");

        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }
}
