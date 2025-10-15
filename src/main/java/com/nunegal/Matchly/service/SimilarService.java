package com.nunegal.Matchly.service;

import com.nunegal.Matchly.client.ExternalClient;
import com.nunegal.Matchly.model.DTO.ProductDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class SimilarService {
    private final ExternalClient client;

    public SimilarService(ExternalClient client) {
        this.client = client;
    }

    public List<ProductDTO> getSimilarDetails(String productId) {
        List<String> ids;
        try {
            ids = client.getSimilarIds(productId);
        } catch (WebClientResponseException.NotFound e) {
            throw e;
        }

        return ids.stream()
                .parallel()
                .map(id -> {
                    try {
                        return client.getProductDetail(id);
                    } catch (WebClientResponseException.NotFound e) {
                        // ignoramos productos no encontrados
                        return null;
                    }
                })
                .filter(p -> p != null)
                .toList();
    }
}
