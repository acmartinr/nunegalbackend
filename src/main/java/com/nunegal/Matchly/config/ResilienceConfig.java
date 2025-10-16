package com.nunegal.Matchly.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig cb = CircuitBreakerConfig.custom()
                .slowCallDurationThreshold(Duration.ofSeconds(7))
                .slowCallRateThreshold(90f)
                .failureRateThreshold(50f)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(10)
                .minimumNumberOfCalls(30)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(java.util.concurrent.TimeoutException.class, java.io.IOException.class)
                .ignoreExceptions(org.springframework.web.reactive.function.client.WebClientResponseException.NotFound.class)
                .slidingWindowSize(100)
                .build();
        return CircuitBreakerRegistry.of(cb);
    }
}
