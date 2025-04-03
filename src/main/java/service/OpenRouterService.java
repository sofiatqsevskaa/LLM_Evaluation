package service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OpenRouterService {

    private final ChatClient chatClient;

    public OpenRouterService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String respond(String inquiry) {
        return chatClient
                .prompt()
                .user(inquiry)
                .call()
                .content();
    }
}
