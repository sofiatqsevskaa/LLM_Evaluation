package model;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Data
public class Inquiry {
    private String inquiry;
    private Map<String, String> answers;

    public Inquiry(String inquiry) {
        this.inquiry = inquiry;
        this.answers = new HashMap<>();
    }

    public void setAnswer(String answer, String model) {
        this.answers.put(model, answer);
    }
}
