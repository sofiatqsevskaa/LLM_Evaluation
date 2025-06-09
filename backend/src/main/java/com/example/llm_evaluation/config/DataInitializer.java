package com.example.llm_evaluation.config;

import com.example.llm_evaluation.model.Answer;
import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.repository.AnswerRepository;
import com.example.llm_evaluation.repository.InquiryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer {

    private final InquiryRepository inquiryRepository;
    private final AnswerRepository answerRepository;

    public DataInitializer(InquiryRepository inquiryRepository, AnswerRepository answerRepository) {
        this.inquiryRepository = inquiryRepository;
        this.answerRepository = answerRepository;
    }

    @PostConstruct
    public void initData() {
        Inquiry inquiry1 = new Inquiry("What is the capital of France?");
        inquiryRepository.save(inquiry1);

        Answer answer1 = new Answer("ModelA", "Paris", inquiry1);
        Answer answer2 = new Answer("ModelB", "Paris", inquiry1);
        answerRepository.saveAll(Arrays.asList(answer1, answer2));

        Inquiry inquiry2 = new Inquiry("What is the largest planet?");
        inquiryRepository.save(inquiry2);

        Answer answer3 = new Answer("ModelA", "Jupiter", inquiry2);
        Answer answer4 = new Answer("ModelB", "Jupiter", inquiry2);
        answerRepository.saveAll(Arrays.asList(answer3, answer4));

    }
}
