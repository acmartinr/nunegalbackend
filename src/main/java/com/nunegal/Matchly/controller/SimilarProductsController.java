package com.nunegal.Matchly.controller;

import com.nunegal.Matchly.model.DTO.ProductDTO;
import com.nunegal.Matchly.service.SimilarService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@RestController
@RequestMapping("/product")
public class SimilarProductsController {
    private final SimilarService service;

    public SimilarProductsController(SimilarService service) {
        this.service = service;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDTO>> similar(@PathVariable @NotBlank String productId) {
        try {
            List<ProductDTO> result = service.getSimilarDetails(productId);
            return ResponseEntity.ok(result);
        } catch (WebClientResponseException.NotFound e) {
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
}

