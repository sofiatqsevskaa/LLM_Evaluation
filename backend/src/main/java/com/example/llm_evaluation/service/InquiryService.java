package com.example.llm_evaluation.service;


import com.example.llm_evaluation.model.Inquiry;

public interface InquiryService {
    Inquiry find(String inquiry);
    Inquiry saveInquiry(String inquiry);
}
