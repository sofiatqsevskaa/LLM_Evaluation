package com.example.llm_evaluation.controller;

import com.example.llm_evaluation.config.OpenRouterConfig;
import com.example.llm_evaluation.model.Answer;
import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.service.AnswerService;
import com.example.llm_evaluation.service.InquiryService;
import com.example.llm_evaluation.service.OpenRouterService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/inquiry")
@CrossOrigin(origins = "http://localhost:3000")
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

    @GetMapping("/models")
    @ResponseBody
    public List<String> getModels() {
        return openRouterConfig.getModels();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamInquiry(@RequestParam String inquiryContent, @RequestParam String model) {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                Inquiry inquiry = Optional.ofNullable(inquiryService.find(inquiryContent))
                        .orElseGet(() -> inquiryService.saveInquiry(inquiryContent));

                String response = openRouterService.respond(inquiryContent, model);
                Answer answer = new Answer(model, response, inquiry);
                answerService.saveAnswer(answer);

                emitter.send(answer); // Send the answer for the specific model
                emitter.complete(); // Done sending

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

}