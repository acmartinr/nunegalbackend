package com.nunegal.Matchly.client;

import com.nunegal.Matchly.model.DTO.ProductDTO;
import com.nunegal.Matchly.validation.InputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Cliente encargado de comunicarse con un servicio externo de búsqueda de productos.
 *
 * <p>Esta clase utiliza {@link WebClient} (reactivo) para realizar peticiones HTTP
 * a endpoints externos, obteniendo tanto los IDs de productos similares como los
 * detalles de cada producto individual.</p>
 */
@Component
public class ExternalClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalClient.class);

    private final WebClient web;
    private static final String SIMILAR_ID_ROUTE = "/product/{id}/similarids";
    private static final String PRODUCT_DETAIL_ROUTE = "/product/{id}";

    @Autowired
    private InputValidator inputValidator;

    public ExternalClient(WebClient externalWebClient) {
        this.web = externalWebClient;
    }


    /**
     * Obtiene los IDs de productos similares a un producto dado.
     *
     * @param productId ID del producto base
     * @return {@link Mono} que emite una lista de IDs similares o vacío si no existen o hay algún error en la llamada
     */
    public Mono<List<String>> getSimilarIds(String productId) {
        inputValidator.validateInputId(productId);
        return web.get()
                .uri(SIMILAR_ID_ROUTE, productId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.createException())
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .doOnError(e -> log.error("Error al obtener similarIds {}: {}", productId, e.toString()))
                .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                    log.warn("Producto {} no encontrado (similarIds)", productId);
                    return Mono.empty();
                });
    }

    /**
     * Obtiene el detalle de un producto específico por su ID.
     *
     * @param id ID del producto
     * @return {@link Mono} que emite el {@link ProductDTO} o vacío si
     * no existe o hay algún error llamando al servicio externo
     */
    public Mono<ProductDTO> getProductDetail(String id) {
        inputValidator.validateInputId(id);
        return web.get()
                .uri(PRODUCT_DETAIL_ROUTE, id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.createException())
                .bodyToMono(ProductDTO.class)
                .doOnError(e -> log.error("❌ Error al obtener producto {}: {}", id, e.getMessage()))
                .onErrorResume(WebClientResponseException.NotFound.class, e -> {
                    log.warn("Producto {} no encontrado (detalle)", id);
                    return Mono.empty();
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Error inesperado con producto {}: {}", id, e.getMessage());
                    return Mono.empty();
                });
    }
}
