package com.example.llm_evaluation.controller;

import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.service.OpenRouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private OpenRouterService openRouterService;

    @PostMapping("/inquiry")
    public String[] getChatResponses(@RequestBody Inquiry inquiry) {
        return openRouterService.getMultipleResponses(inquiry);
    }
}
