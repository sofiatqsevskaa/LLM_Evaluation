package com.example.llm_evaluation.service.impl;

import com.example.llm_evaluation.model.Answer;
import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.repository.AnswerRepository;
import com.example.llm_evaluation.service.AnswerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public void saveAnswer(Answer answer) {
        answerRepository.save(answer);
    }

    @Override
    public Optional<Answer> findByInquiryAndModel(Inquiry inquiry, String model) {
        return answerRepository.findByInquiryAndModel(inquiry, model);
    }

    @Override
    public Optional<Answer> findByInquiryIdAndModel(Long inquiryId, String model) {
        return answerRepository.findByInquiryIdAndModel(inquiryId, model);
    }

}
