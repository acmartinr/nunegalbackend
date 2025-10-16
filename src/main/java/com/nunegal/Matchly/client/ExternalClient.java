package com.nunegal.Matchly.client;

import com.nunegal.Matchly.model.DTO.ProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ExternalClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalClient.class);

    private final WebClient web;
    private static final String SIMILAR_ID_ROUTE = "/product/{id}/similarids";
    private static final String PRODUCT_DETAIL_ROUTE = "/product/{id}";

    public ExternalClient(WebClient externalWebClient) {
        this.web = externalWebClient;
    }

    public Mono<List<String>> getSimilarIds(String productId) {
        return web.get()
                .uri(SIMILAR_ID_ROUTE, productId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.createException())
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .doOnError(e -> log.error("❌ Error al obtener similarIds {}: {}", productId, e.toString()))
                .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                    log.warn("⚠️ Producto {} no encontrado (similarIds)", productId);
                    return Mono.empty();
                });
    }

    public Mono<ProductDTO> getProductDetail(String id) {
        return web.get()
                .uri(PRODUCT_DETAIL_ROUTE, id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.createException())
                .bodyToMono(ProductDTO.class)
                .doOnError(e -> log.error("❌ Error al obtener producto {}: {}", id, e.getMessage()))
                .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                    log.warn("⚠️ Producto {} no encontrado (detalle)", id);
                    return Mono.empty();
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("💥 Error inesperado con producto {}: {}", id, e.getMessage());
                    return Mono.empty();
                });
    }
}
