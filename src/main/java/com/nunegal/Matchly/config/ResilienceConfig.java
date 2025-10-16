package com.nunegal.Matchly.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Value("${resilience.slow-call-duration-threshold-seconds:7}")
    private int slowCallDurationThreshold;

    @Value("${resilience.slow-call-rate-threshold:90}")
    private float slowCallRateThreshold;

    @Value("${resilience.failure-rate-threshold:50}")
    private float failureRateThreshold;

    @Value("${resilience.wait-duration-open-seconds:10}")
    private int waitDurationInOpenState;

    @Value("${resilience.permitted-calls-half-open:10}")
    private int permittedNumberOfCallsInHalfOpenState;

    @Value("${resilience.minimum-calls:30}")
    private int minimumNumberOfCalls;

    @Value("${resilience.auto-transition:true}")
    private boolean autoTransition;

    @Value("${resilience.sliding-window-size:100}")
    private int slidingWindowSize;

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig cb = CircuitBreakerConfig.custom()
                .slowCallDurationThreshold(Duration.ofSeconds(slowCallDurationThreshold))
                .slowCallRateThreshold(slowCallRateThreshold)
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitDurationInOpenState))
                .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .automaticTransitionFromOpenToHalfOpenEnabled(autoTransition)
                .recordExceptions(java.util.concurrent.TimeoutException.class, java.io.IOException.class)
                .ignoreExceptions(WebClientResponseException.NotFound.class)
                .slidingWindowSize(slidingWindowSize)
                .build();

        return CircuitBreakerRegistry.of(cb);
    }
}
