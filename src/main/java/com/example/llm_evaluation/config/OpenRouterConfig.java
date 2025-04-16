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

    private static final List<String> MODELS = Arrays.asList("agentica-org/deepcoder-14b-preview:free", "deepseek/deepseek-chat-v3-0324:free", "shisa-ai/shisa-v2-llama3.3-70b:free", "qwen/qwq-32b:free");

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
