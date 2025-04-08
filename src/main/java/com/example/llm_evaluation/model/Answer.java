package com.example.llm_evaluation.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
@Entity
@Data

public class Answer {

    public Answer() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String model;

    private String answer;

    @ManyToOne
    private Inquiry inquiry;

    public Answer(String model, String answer, Inquiry inquiry) {
        this.model = model;
        this.answer = answer;
        this.inquiry = inquiry;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }


    public String getModel() {
        return model;
    }

    public String getAnswer() {
        return answer;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }
}
