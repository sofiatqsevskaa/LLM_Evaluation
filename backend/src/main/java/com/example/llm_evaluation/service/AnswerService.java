package com.example.llm_evaluation.service;

import com.example.llm_evaluation.model.Answer;
import com.example.llm_evaluation.model.Inquiry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AnswerService {
    List<Answer> findByInquiry(Inquiry inquiry);
    void saveAnswer(Answer answer);
}
