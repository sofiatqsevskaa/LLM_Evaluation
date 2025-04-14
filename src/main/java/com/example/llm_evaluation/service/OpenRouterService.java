package com.example.llm_evaluation.service;

import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.config.OpenRouterConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class OpenRouterService {

    private final RestTemplate restTemplate;
    private final OpenRouterConfig openRouterConfig;

    public OpenRouterService(RestTemplate restTemplate, OpenRouterConfig openRouterConfig) {
        this.restTemplate = restTemplate;
        this.openRouterConfig = openRouterConfig;
    }

    public String respond(String inquiry, String model) {
        String apiUrl = openRouterConfig.getApiUrl();


        String requestBody = "{"
                + "\"model\": \"" + model + "\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + inquiry + "\"}]"
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openRouterConfig.getApiKey());
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();
    }

    public String[] getMultipleResponses(Inquiry inquiry) {
        String[] responses = new String[openRouterConfig.getModels().size()];

        for (int i = 0; i < openRouterConfig.getModels().size(); i++) {
            String model = openRouterConfig.getModels().get(i);
            responses[i] = respond(inquiry.getInquiry(), model);
        }

        return responses;
    }
}
