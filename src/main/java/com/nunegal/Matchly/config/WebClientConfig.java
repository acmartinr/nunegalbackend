package com.nunegal.Matchly.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient externalWebClient(
            @Value("${external.base-url}") String baseUrl,
            @Value("${external.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${external.response-timeout-ms}") int responseTimeoutMs,
            @Value("${external.request-timeout-ms}") int requestTimeoutMs,
            // pool
            @Value("${external.pool.max-connections:200}") int maxConnections,
            @Value("${external.pool.pending-acquire-timeout-ms:500}") long pendingAcquireTimeoutMs,
            @Value("${external.pool.max-idle-seconds:15}") long maxIdleSeconds,
            @Value("${external.pool.max-life-minutes:1}") long maxLifeMinutes
    ) {
        ConnectionProvider pool = ConnectionProvider.builder("external-pool")
                .maxConnections(maxConnections)
                .pendingAcquireTimeout(Duration.ofMillis(pendingAcquireTimeoutMs))
                .maxIdleTime(Duration.ofSeconds(maxIdleSeconds))
                .maxLifeTime(Duration.ofMinutes(maxLifeMinutes))
                .build();

        HttpClient http = HttpClient.create(pool)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofMillis(responseTimeoutMs))
                .doOnConnected(c -> c
                        .addHandlerLast(new ReadTimeoutHandler(responseTimeoutMs, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(responseTimeoutMs, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(http))
                .filter((req, next) -> next.exchange(req).timeout(Duration.ofMillis(requestTimeoutMs)))
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
