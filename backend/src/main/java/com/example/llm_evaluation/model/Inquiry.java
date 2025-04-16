package com.example.llm_evaluation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;


@Entity
@Data
@Getter
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String inquiry;

    public Inquiry(String inquiry) {
        this.inquiry = inquiry;
    }

    public Inquiry() {
    }

    public String getInquiry() {
        return inquiry;
    }
}
