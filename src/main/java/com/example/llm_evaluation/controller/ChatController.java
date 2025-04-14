package com.example.llm_evaluation.controller;

import com.example.llm_evaluation.model.Answer;
import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.service.AnswerService;
import com.example.llm_evaluation.service.InquiryService;
import com.example.llm_evaluation.service.OpenRouterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ChatController {
    private final OpenRouterService openRouterService;
    private final InquiryService inquiryService;
    private final AnswerService answerService;

    public ChatController(OpenRouterService openRouterService, InquiryService inquiryService, AnswerService answerService) {
        this.openRouterService = openRouterService;
        this.inquiryService = inquiryService;
        this.answerService = answerService;
    }

    @PostMapping("/inquiry")
    @ResponseBody
    public Long createInquiry(@RequestParam String inquiryContent) {
        Inquiry inquiry = Optional.ofNullable(inquiryService.find(inquiryContent))
                .orElseGet(() -> inquiryService.saveInquiry(inquiryContent));
        return inquiry.getId();
    }
}