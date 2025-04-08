package service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OpenRouterService {

    private final ChatClient chatClient;

    public OpenRouterService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String respond(String inquiry, String model) {
        return chatClient
                .prompt()
                .user(inquiry)
                .call()
                .content();
    }

    public String[] getMultipleResponses(String inquiry) {
        String[] responses = new String[4];

        String[] models = { "gpt-3.5", "gpt-4", "other-model-1", "other-model-2" };

        for (int i = 0; i < models.length; i++) {
            responses[i] = respond(inquiry, models[i]);
        }

        return responses;
    }
}
