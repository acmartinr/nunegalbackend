package com.nunegal.Matchly.service;

import com.nunegal.Matchly.client.ExternalClient;
import com.nunegal.Matchly.model.DTO.ProductDTO;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Servicio encargado de obtener productos similares, combinando llamadas externas
 * a los endpoints de IDs y detalles de productos.
 *
 * <p>Este servicio implementa control de resiliencia usando Resilience4j:
 */
@Service
public class SimilarService {

    private final ExternalClient client;
    private final CircuitBreaker cbIds;
    private final CircuitBreaker cbDetail;
    private static final Logger log = LoggerFactory.getLogger(ExternalClient.class);

    public SimilarService(ExternalClient client, CircuitBreakerRegistry registry) {
        this.client = client;
        CircuitBreakerRegistry reg = (registry != null) ? registry : CircuitBreakerRegistry.ofDefaults();
        this.cbIds = reg.circuitBreaker("ext-ids");
        this.cbDetail = reg.circuitBreaker("ext-detail");
    }


    /**
     * Obtiene una lista de {@link ProductDTO} similares a un producto dado.
     *
     * @param productId ID del producto base
     * @return Mono con la lista de productos similares (vacía si no hay o existe algún fallo
     */
    public Mono<List<ProductDTO>> getSimilarDetails(String productId) {
        return client.getSimilarIds(productId)
                // CB para la obtención de IDs
                .transformDeferred(CircuitBreakerOperator.of(cbIds))
                .flatMapMany(Flux::fromIterable)
                .flatMap(id ->
                                client.getProductDetail(id)
                                        // CB por cada detalle
                                        .transformDeferred(CircuitBreakerOperator.of(cbDetail)),
                        /*concurrency*/ 10
                )
                .onErrorContinue((ex, val) -> log.warn("Detalle de producto falló para id {}: {}", val, ex.toString()))
                .collectList()
                // Si el breaker de IDs está abierto → devuelve vacío rápido
                .onErrorResume(io.github.resilience4j.circuitbreaker.CallNotPermittedException.class,
                        e -> Mono.just(List.of()));
    }
}
