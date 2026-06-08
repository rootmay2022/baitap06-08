package com.ApiGateway.ApiGateway.config;

import com.ApiGateway.ApiGateway.filter.SessionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    @Bean
    public SessionFilter sessionFilter() {
        return new SessionFilter();
    }
}