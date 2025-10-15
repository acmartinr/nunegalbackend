package com.nunegal.Matchly.service;

import com.nunegal.Matchly.client.ExternalClient;
import com.nunegal.Matchly.model.DTO.ProductDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimilarServiceTest {

    @Test
    void agregaYOmiteNulls() {
        ExternalClient client = mock(ExternalClient.class);
        SimilarService service = new SimilarService(client);

        when(client.getSimilarIds("1")).thenReturn(List.of("2", "3", "3"));
        when(client.getProductDetail("2")).thenReturn(new ProductDTO("2", "P2", 10.0, true));
        when(client.getProductDetail("3")).thenReturn(null); // simula 404/timeout omitido

        List<ProductDTO> out = service.getSimilarDetails("1");

        assertEquals(1, out.size());
        assertEquals("2", out.get(0).getId());
        verify(client).getSimilarIds("1");
        verify(client, times(1)).getProductDetail("2");
        verify(client, times(1)).getProductDetail("3");
    }

    @Test
    void siSimilarIds404_propagasExcepcion() {
        ExternalClient client = mock(ExternalClient.class);
        SimilarService service = new SimilarService(client);

        when(client.getSimilarIds("x")).thenThrow(WebClientResponseException.create(404, "Not Found", null, null, null));

        assertThrows(WebClientResponseException.class, () -> service.getSimilarDetails("x"));
    }
}
