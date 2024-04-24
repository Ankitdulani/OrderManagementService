package com.kmbl.OrderManagementService.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@ComponentScan
public class Configuration {

    @Bean
    public RestTemplate restTemplate(){
        // TODO: add configuration to retry the rest calls;
        return new RestTemplate();
    }

}
