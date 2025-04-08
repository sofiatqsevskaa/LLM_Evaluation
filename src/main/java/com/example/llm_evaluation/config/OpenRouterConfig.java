package com.example.llm_evaluation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "openrouter")
public class OpenRouterConfig {

    private String apiKey;
    private String apiUrl;
    private List<String> models;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public List<String> getModels() {
        return models;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
