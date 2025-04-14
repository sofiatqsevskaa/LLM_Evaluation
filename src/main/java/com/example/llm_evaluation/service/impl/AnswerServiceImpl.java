package com.example.llm_evaluation.service.impl;

import com.example.llm_evaluation.model.Answer;
import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.repository.AnswerRepository;
import com.example.llm_evaluation.service.AnswerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    public List<Answer> findByInquiry(Inquiry inquiry) {
        return answerRepository.findByInquiry(inquiry);
    }
}
