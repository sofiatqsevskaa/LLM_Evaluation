package com.example.llm_evaluation.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
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
