package com.nunegal.Matchly.service;

import com.nunegal.Matchly.client.ExternalClient;
import com.nunegal.Matchly.model.DTO.ProductDTO;
import com.nunegal.Matchly.validation.InputValidator;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static org.mockito.Mockito.*;

class SimilarServiceTest {
    private final InputValidator validator = new InputValidator();
    private static String MESG_EXCEPTION_EMPTY = "El ID no puede estar vacío";

    @Test
    void validateInputId_null_lanzaExcepcion() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateInputId(null));
        assertTrue(ex.getMessage().equals(MESG_EXCEPTION_EMPTY));
    }

    @Test
    void validateInputId_blanco_lanzaExcepcion() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateInputId("   "));
        assertTrue(ex.getMessage().equals(MESG_EXCEPTION_EMPTY));
    }

    @Test
    void validateInputId_ok_noLanzaExcepcion() {
        assertDoesNotThrow(() -> validator.validateInputId("123"));
    }


    @Test
    void agregaYOmiteNulls_oVacios() {
        ExternalClient client = mock(ExternalClient.class);
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults(); // 👈 necesario
        SimilarService service = new SimilarService(client, registry);        // 👈 usa el ctor correcto

        // similarIds: devuelve [2,3,3]
        when(client.getSimilarIds("1"))
                .thenReturn(Mono.just(List.of("2", "3", "3")));

        // Detalle "2" OK
        when(client.getProductDetail("2"))
                .thenReturn(Mono.just(new ProductDTO("2", "P2", 10.0, true)));

        // Detalle "3" vacío (equivale a 404 manejado en el client)
        when(client.getProductDetail("3"))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.getSimilarDetails("1"))
                .expectNextMatches(list -> list.size() == 1 && "2" .equals(list.get(0).getId()))
                .verifyComplete();

        verify(client).getSimilarIds("1");
        verify(client, times(1)).getProductDetail("2");
        // se llamará dos veces a "3" porque ids trae "3" duplicado
        verify(client, times(2)).getProductDetail("3");
        verifyNoMoreInteractions(client);
    }
}
