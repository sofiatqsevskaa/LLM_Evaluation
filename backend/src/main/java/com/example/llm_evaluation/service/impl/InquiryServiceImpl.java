package com.example.llm_evaluation.service.impl;

import com.example.llm_evaluation.model.Inquiry;
import com.example.llm_evaluation.service.InquiryService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.llm_evaluation.repository.InquiryRepository;

@Service
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;

    public InquiryServiceImpl(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    @Override
    public Inquiry find(String inquiry) {
        return inquiryRepository.findByInquiry(inquiry).orElse(null);
    }

    @Override
    public synchronized Inquiry saveInquiry(String inquiryContent) {
        try {
            return inquiryRepository.findByInquiry(inquiryContent)
                    .orElseGet(() -> inquiryRepository.save(new Inquiry(inquiryContent)));
        } catch (DataIntegrityViolationException e) {
            return inquiryRepository.findByInquiry(inquiryContent)
                    .orElseThrow(() -> new IllegalArgumentException("Duplicate inquiry detected: " + inquiryContent, e));
        }
    }
}