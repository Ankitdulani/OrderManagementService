package com.kmbl.OrderManagementService.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
@Configuration
public class WebClientConfig {

    @Value("${inventoryManagementService.endpoint}")
    private String inventoryManagementServiceEndpoint;

    @Bean
    public WebClient webClient(){
        return WebClient.builder().baseUrl(inventoryManagementServiceEndpoint).build();
    }
}
