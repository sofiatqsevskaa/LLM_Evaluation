package com.example.llm_evaluation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@SpringBootApplication
public class LlmEvaluationApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlmEvaluationApplication.class, args);
    }

}
