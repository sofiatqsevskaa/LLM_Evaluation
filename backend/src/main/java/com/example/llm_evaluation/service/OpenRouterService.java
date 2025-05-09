package com.example.llm_evaluation.service;

import com.example.llm_evaluation.config.OpenRouterConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.*;

import java.nio.charset.StandardCharsets;


@Service
public class OpenRouterService {
    private final RestTemplate restTemplate;
    private final OpenRouterConfig openRouterConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenRouterService(RestTemplate restTemplate, OpenRouterConfig openRouterConfig) {
        this.restTemplate = restTemplate;
        this.openRouterConfig = openRouterConfig;
    }

    public String respond(String inquiry, String model) {
        String apiUrl = openRouterConfig.getApiUrl();

        String messagesJson = getString(inquiry, model);
        System.out.println("Request: " + messagesJson);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openRouterConfig.getApiKey());
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        HttpEntity<String> entity = new HttpEntity<>(messagesJson, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
        System.out.println("Response: " + response.getBody());

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.path("choices");

            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode messageNode = choices.get(0).path("message").path("content");
                return messageNode.isMissingNode() ? null : messageNode.asText();
            } else {
                return null;
            }
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
    }

    private static String getString(String inquiry, String model) {
        if (inquiry.contains("<image>") && inquiry.contains("</image>")) {
            String base64Image = inquiry.substring(
                    inquiry.indexOf("<image>") + 7,
                    inquiry.indexOf("</image>")
            );

            return "{"
                    + "\"model\": \"" + model + "\","
                    + "\"messages\": [{"
                    + "\"role\": \"user\", "
                    + "\"content\": ["
                    + "{"
                    + "\"type\": \"text\", "
                    + "\"text\": \"" + inquiry.replaceAll("<image>.*?</image>", "").trim() + "\""
                    + "}, "
                    + "{"
                    + "\"type\": \"image_url\", "
                    + "\"image_url\": {"
                    + "\"url\": \"" + base64Image + "\""
                    + "}"
                    + "}"
                    + "]"
                    + "}]"
                    + "}";
    } else {
        return "{"
                + "\"model\": \"" + model + "\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + inquiry + "\"}]"
                + "}";
    }
}
}