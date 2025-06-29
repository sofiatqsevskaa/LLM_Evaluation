package com.example.llm_evaluation.service;


import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.model.Answer;

import java.util.List;
import java.util.Optional;

public interface InquiryService {
    List<Inquiry> findAll();
    Inquiry find(String inquiry);
    List<Answer> findAnswers(String inquiry);
    Inquiry saveInquiry(String inquiry);
    Optional<Inquiry> findById(Long inquiryId);
}
