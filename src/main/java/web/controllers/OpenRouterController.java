package web.controllers;

import model.Inquiry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.OpenRouterService;

@RestController
public class OpenRouterController {

    private final OpenRouterService openRouterService;

    public OpenRouterController(OpenRouterService openRouterService) {
        this.openRouterService = openRouterService;
    }

    @PostMapping("/chat")
    public String[] chat(@RequestBody Inquiry inquiry) {
        String inquiryText = inquiry.getInquiry();

        return openRouterService.getMultipleResponses(inquiryText);
    }
}
