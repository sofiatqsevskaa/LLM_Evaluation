package com.example.llm_evaluation.repository;
import com.example.llm_evaluation.model.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Inquiry findByInquiry(String inquiry);
}
