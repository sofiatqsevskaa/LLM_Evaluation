package com.example.llm_evaluation.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

@Entity
@Data
@NoArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String model;
    @Lob
    @Column
    private String answer;
    @ManyToOne
    private Inquiry inquiry;

    public Answer(String model, String answer, Inquiry inquiry) {
        this.model = model;
        this.answer = answer;
        this.inquiry = inquiry;
    }

    public String getAnswer() {
        return answer;
    }

    public String getModel() {
        return model;
    }
}
