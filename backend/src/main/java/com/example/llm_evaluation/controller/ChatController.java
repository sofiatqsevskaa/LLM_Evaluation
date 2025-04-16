package com.example.llm_evaluation.controller;

import com.example.llm_evaluation.config.OpenRouterConfig;
import com.example.llm_evaluation.model.Answer;
import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.service.AnswerService;
import com.example.llm_evaluation.service.InquiryService;
import com.example.llm_evaluation.service.OpenRouterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

@Controller
public class ChatController {
    private final OpenRouterService openRouterService;
    private final InquiryService inquiryService;
    private final AnswerService answerService;
    private final OpenRouterConfig openRouterConfig;

    public ChatController(OpenRouterService openRouterService, InquiryService inquiryService, AnswerService answerService, OpenRouterConfig openRouterConfig) {
        this.openRouterService = openRouterService;
        this.inquiryService = inquiryService;
        this.answerService = answerService;
        this.openRouterConfig = openRouterConfig;
    }

    @GetMapping("/inquiry/stream")
    public SseEmitter streamInquiry(@RequestParam String inquiryContent) {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                Inquiry inquiry = Optional.ofNullable(inquiryService.find(inquiryContent))
                        .orElseGet(() -> inquiryService.saveInquiry(inquiryContent));

                for (String model : openRouterConfig.getModels()) {
                    String response = openRouterService.respond(inquiryContent, model);
                    Answer answer = new Answer(model, response, inquiry);
                    answerService.saveAnswer(answer);

                    emitter.send(answer); // Send each answer immediately
                }

                emitter.complete(); // Done sending

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

}