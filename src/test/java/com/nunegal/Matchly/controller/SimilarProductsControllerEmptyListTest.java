package com.nunegal.Matchly.controller;

import com.nunegal.Matchly.service.SimilarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

@WebFluxTest(SimilarProductsController.class)
@AutoConfigureWebTestClient
@Import(GlobalExceptionHandler.class)
@SuppressWarnings("removal") // por @MockBean deprecado en Boot 3.4 (aún válido)
class SimilarProductsControllerEmptyListTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SimilarService similarService;

    @Test
    void cuandoServiceLanzaNotFound_entonces200ConListaVacia() {
        var notFound = WebClientResponseException.create(404, "Not Found", null, new byte[0], StandardCharsets.UTF_8);

        when(similarService.getSimilarDetails("nf")).thenReturn(Mono.error(notFound));

        webTestClient.get()
                .uri("/product/nf/similar")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }

    @Test
    void cuandoServiceLanzaErrorGenerico_entonces500ConListaVacia() {
        when(similarService.getSimilarDetails("err")).thenReturn(Mono.error(new RuntimeException("500")));

        webTestClient.get()
                .uri("/product/err/similar")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody().json("[]");
    }
}

