package com.nunegal.Matchly.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nunegal.Matchly.model.DTO.ProductDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class ExternalClient {
    private static final Logger log = LoggerFactory.getLogger(ExternalClient.class);
    private final WebClient web;
    private static String SIMILAR_ID_ROUTE = "/product/{id}/similarids";
    private static String PRODUCT_DETAIL_ROUTE = "/product/{id}";

    public ExternalClient(WebClient externalWebClient) {
        this.web = externalWebClient;
    }

    public List<String> getSimilarIds(String productId) {
        try {
            return web.get()
                    .uri(SIMILAR_ID_ROUTE, productId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, r -> r.createException())
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                    })
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Product with id {} not found", productId);
            throw e;
        }
    }

    public ProductDTO getProductDetail(String id) {
        try {
            return web.get()
                    .uri(PRODUCT_DETAIL_ROUTE, id)
                    .retrieve()
                    .onStatus(status -> status.isError(), r -> r.createException())
                    .bodyToMono(ProductDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Product with id {} not found", id);
            return null;
        } catch (Exception e) {
            log.error("Error fetching product with id {}: {}", id, e.getMessage());
            return null;
        }
    }
}
