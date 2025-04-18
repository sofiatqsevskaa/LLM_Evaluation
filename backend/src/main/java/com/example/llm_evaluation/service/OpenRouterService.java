package com.example.llm_evaluation.service;

import com.example.llm_evaluation.model.Answer;
import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.config.OpenRouterConfig;
import com.example.llm_evaluation.repository.AnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenRouterService {
    private final AnswerRepository answerRepository;
    private final RestTemplate restTemplate;
    private final OpenRouterConfig openRouterConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OpenRouterService(AnswerRepository answerRepository, RestTemplate restTemplate, OpenRouterConfig openRouterConfig) {
        this.answerRepository = answerRepository;
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
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
        System.out.println("Response: " + response.getBody());
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.path("choices");

            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode messageNode = choices.get(0).path("message").path("content");
                if (!messageNode.isMissingNode()) {
                    return messageNode.asText();
                } else {
                    return "Error: Missing 'content' field in response";
                }
            } else {
                return "Error: 'choices' array is missing or empty";
            }

        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
    }

    public List<Answer> getMultipleResponses(Inquiry inquiry) {
        String[] responses = new String[openRouterConfig.getModels().size()];
        List<Answer> answers = new ArrayList<>();
        for (int i = 0; i < openRouterConfig.getModels().size(); i++) {
            String model = openRouterConfig.getModels().get(i);
            responses[i] = respond(inquiry.getInquiry(), model);
            Answer answer = new Answer(model, responses[i], inquiry);
            answerRepository.save(answer);
            answers.add(answer);
        }
        return answers;
    }
}
