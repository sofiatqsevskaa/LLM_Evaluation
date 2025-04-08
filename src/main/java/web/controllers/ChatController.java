package web.controllers;

import service.OpenRouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private OpenRouterService openRouterService;

    @PostMapping("/inquiry")
    public String[] getChatResponses(@RequestBody String inquiry) {
        // Get responses from multiple models
        return openRouterService.getMultipleResponses(inquiry);
    }
}
