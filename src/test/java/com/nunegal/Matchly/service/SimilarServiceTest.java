package com.nunegal.Matchly.service;

import com.nunegal.Matchly.client.ExternalClient;
import com.nunegal.Matchly.model.DTO.ProductDTO;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;

class SimilarServiceTest {

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

    @Test
    void siSimilarIds404_devuelveListaVacia() {
        ExternalClient client = mock(ExternalClient.class);
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        SimilarService service = new SimilarService(client, registry);

        // El ExternalClient real para 404 devuelve Mono.empty(), no error:
        when(client.getSimilarIds("x")).thenReturn(Mono.empty());

        StepVerifier.create(service.getSimilarDetails("x"))
                .expectNext(List.of())   // lista vacía
                .verifyComplete();

        verify(client).getSimilarIds("x");
        verifyNoMoreInteractions(client);
    }

    @Test
    void siSimilarIds500_propagasError() {
        ExternalClient client = mock(ExternalClient.class);
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        SimilarService service = new SimilarService(client, registry);

        WebClientResponseException internal =
                WebClientResponseException.create(500, "Boom", null, new byte[0], StandardCharsets.UTF_8);

        when(client.getSimilarIds("err")).thenReturn(Mono.error(internal));

        StepVerifier.create(service.getSimilarDetails("err"))
                .expectError(WebClientResponseException.class)
                .verify();

        verify(client).getSimilarIds("err");
        verifyNoMoreInteractions(client);
    }
}
