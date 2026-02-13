package br.com.fiap.mschatgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PharmacyConfig {

    @Bean
    public WebClient pharmacyWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8088")
                .build();
    }
}
