package com.example.llm_evaluation.controller;

import com.example.llm_evaluation.config.OpenRouterConfig;
import com.example.llm_evaluation.model.Answer;
import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.service.AnswerService;
import com.example.llm_evaluation.service.InquiryService;
import com.example.llm_evaluation.service.OpenRouterService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;
import java.util.Map;


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

                emitter.send(answer);
                emitter.complete();

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    @PostMapping(value = "/stream/image", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamImagePost(@RequestBody Map<String, String> payload) {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                String model = payload.get("model");
                String base64Image = payload.get("image");
                String prompt = payload.getOrDefault("prompt", "What's in this image?");
                String inquiryContent = prompt + " <image>" + base64Image + "</image>";

                Inquiry inquiry = Optional.ofNullable(inquiryService.find(inquiryContent))
                        .orElseGet(() -> inquiryService.saveInquiry(inquiryContent));

                String response = openRouterService.respond(inquiryContent, model);

                if (response != null) {
                    Answer answer = new Answer(model, response, inquiry);
                    answerService.saveAnswer(answer);
                    emitter.send(SseEmitter.event().data(answer).build());
                }

                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }



}