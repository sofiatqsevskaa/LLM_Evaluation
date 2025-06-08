package com.example.llm_evaluation.service;

import com.example.llm_evaluation.model.Answer;
import com.example.llm_evaluation.model.Inquiry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AnswerService {
    List<Answer> findByInquiry(Inquiry inquiry);
    void saveAnswer(Answer answer);
    Optional<Answer> findByInquiryAndModel(Inquiry inquiry, String model);
    Optional<Answer> findByInquiryIdAndModel(Long inquiryId, String model);
}
