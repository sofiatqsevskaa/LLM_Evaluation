package com.example.llm_evaluation.service;

import com.example.llm_evaluation.model.Answer;
import org.springframework.stereotype.Service;


@Service
public interface AnswerService {
    void saveAnswer(Answer answer);
}
