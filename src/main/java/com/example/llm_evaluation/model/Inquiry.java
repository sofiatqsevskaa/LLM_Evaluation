package com.example.llm_evaluation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Data
@Getter

public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
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
