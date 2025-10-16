package com.nunegal.Matchly.controller;

import com.nunegal.Matchly.model.DTO.ProductDTO;
import com.nunegal.Matchly.service.SimilarService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Controlador REST que expone los endpoints relacionados con productos similares.
 * <p>
 * Devuelve la lista de productos similares asociados al producto especificado.
 */
@RestController
@RequestMapping("/product")
public class SimilarProductsController {

    private final SimilarService service;

    public SimilarProductsController(SimilarService service) {
        this.service = service;
    }

    @GetMapping("/{productId}/similar")
    public Mono<ResponseEntity<List<ProductDTO>>> getSimilarProducts(@PathVariable @NotBlank String productId) {

        return service.getSimilarDetails(productId)
                // Si todo va bien → OK con la lista
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientResponseException.NotFound.class,
                        e -> Mono.just(ResponseEntity.ok(List.of())))
                .onErrorResume(Exception.class,
                        e -> Mono.just(ResponseEntity.internalServerError().body(List.of())));
    }
}
