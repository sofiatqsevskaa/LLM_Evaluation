package com.example.llm_evaluation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenRouterConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    private static final List<String> MODELS = Arrays.asList("tngtech/deepseek-r1t-chimera:free", "arliai/qwq-32b-arliai-rpr-v1:free", "microsoft/mai-ds-r1:free", "qwen/qwen2.5-vl-3b-instruct:free");

    public String getApiKey() {
        return apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public List<String> getModels() {
        return MODELS;
    }

}
