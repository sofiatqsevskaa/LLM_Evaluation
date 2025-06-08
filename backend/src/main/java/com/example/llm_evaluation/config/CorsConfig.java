package com.example.llm_evaluation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/inquiry/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("Content-Type")
                .exposedHeaders("Access-Control-Allow-Origin")
                .allowCredentials(true);
        registry.addMapping("/models")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET")
                .allowCredentials(true);
    }
}