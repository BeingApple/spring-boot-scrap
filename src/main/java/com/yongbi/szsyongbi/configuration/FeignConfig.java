package com.yongbi.szsyongbi.configuration;

import feign.Client;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    private final String apiKey;

    public FeignConfig(@Value("${scrap.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Accept", "application/json");
            requestTemplate.header("X-API-KEY", apiKey);
        };
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Client client() {
        return new Client.Default(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getHostnameVerifier());
    }
}
