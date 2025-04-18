package com.example.llm_evaluation.service.impl;

import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.service.InquiryService;
import com.example.llm_evaluation.model.Answer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.llm_evaluation.repository.AnswerRepository;
import com.example.llm_evaluation.repository.InquiryRepository;

import java.util.List;

@Service
public class InquiryServiceImpl implements InquiryService {

    private final AnswerRepository answerRepository;
    private final InquiryRepository inquiryRepository;

    public InquiryServiceImpl(AnswerRepository answerRepository, InquiryRepository inquiryRepository) {
        this.answerRepository = answerRepository;
        this.inquiryRepository = inquiryRepository;
    }

    @Override
    public List<Inquiry> findAll() {
        return inquiryRepository.findAll();
    }

    @Override
    public Inquiry find(String inquiry) {
        return inquiryRepository.findByInquiry(inquiry).orElse(null);
    }

    @Override
    public List<Answer> findAnswers(String inquiry) {
        Inquiry inq = inquiryRepository.findByInquiry(inquiry).orElse(null);
        if (inq == null) {
            throw new IllegalArgumentException("Inquiry not found: " + inquiry);
        }
        return answerRepository.findByInquiry(inq);
    }

    @Override
    public synchronized Inquiry saveInquiry(String inquiryContent) {
        try {
            // Check if the inquiry already exists
            return inquiryRepository.findByInquiry(inquiryContent)
                    .orElseGet(() -> inquiryRepository.save(new Inquiry(inquiryContent)));
        } catch (DataIntegrityViolationException e) {
            // Handle race condition where another thread inserts the same inquiry
            return inquiryRepository.findByInquiry(inquiryContent)
                    .orElseThrow(() -> new IllegalArgumentException("Duplicate inquiry detected: " + inquiryContent, e));
        }
    }
}