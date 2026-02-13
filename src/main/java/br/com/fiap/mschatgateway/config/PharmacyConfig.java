package br.com.fiap.mschatgateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PharmacyConfig {

    @Value("${backend.base-url}")
    private String backendBaseUrl;


    @Bean
    public WebClient pharmacyWebClient() {
        return WebClient.builder()
                .baseUrl(backendBaseUrl)
                .build();
    }
}
