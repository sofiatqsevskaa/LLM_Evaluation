package service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LlmEvaluationService {

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public LlmEvaluationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
    }

    public Mono<String> evaluateWithOpenAi(String inputText) {
        return webClient.post()
                .uri("/openai-endpoint")
                .bodyValue("{\"prompt\":\"" + inputText + "\", \"model\": \"gpt-3.5-turbo\"}")
                .header("Authorization", "Bearer " + openAiApiKey)
                .retrieve()
                .bodyToMono(String.class);
    }
}
