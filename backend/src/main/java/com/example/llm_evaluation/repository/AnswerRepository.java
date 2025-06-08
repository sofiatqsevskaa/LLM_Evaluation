package com.example.llm_evaluation.repository;

import com.example.llm_evaluation.model.Answer;
import com.example.llm_evaluation.model.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByInquiry(Inquiry inquiry);
    Optional<Answer> findByInquiryAndModel(Inquiry inquiry, String model);
    Optional<Answer> findByInquiryIdAndModel(Long inquiryId, String model);
}
