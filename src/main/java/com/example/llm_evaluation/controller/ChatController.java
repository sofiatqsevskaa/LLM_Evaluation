package com.example.llm_evaluation.controller;

import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.service.InquiryService;
import com.example.llm_evaluation.service.OpenRouterService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private OpenRouterService openRouterService;
    private final InquiryService inquiryService;

    public ChatController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping("/inquiry")
    public String[] getChatResponses(@RequestParam String inquiryContent) {
        System.out.println("Inquiry: "+inquiryContent);
        Inquiry inquiry = Optional.ofNullable(inquiryService.find(inquiryContent))
                .orElseGet(() -> inquiryService.saveInquiry(inquiryContent));
        return openRouterService.getMultipleResponses(inquiry);
    }

}
