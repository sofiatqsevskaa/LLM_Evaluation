package com.example.llm_evaluation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/inquiry/stream")
                .allowedOrigins("http://localhost:3000") // React dev server
                .allowedMethods("GET")
                .allowCredentials(true);
        registry.addMapping("/models")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET")
                .allowCredentials(true);
    }
}