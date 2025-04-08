package com.example.llm_evaluation.controller;

import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.service.InquiryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.llm_evaluation.service.OpenRouterService;

@RestController
public class OpenRouterController {

    private final OpenRouterService openRouterService;
    private final InquiryService inquiryService;

    public OpenRouterController(OpenRouterService openRouterService, InquiryService inquiryService) {
        this.openRouterService = openRouterService;
        this.inquiryService = inquiryService;
    }

    @PostMapping("/chat")
    public String[] chat(@RequestBody String inquiry) {
        Inquiry inq = inquiryService.find(inquiry);
        return openRouterService.getMultipleResponses(inq);
    }
}

