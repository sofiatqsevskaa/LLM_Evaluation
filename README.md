# LLM Evaluation App

This is a simple web application for evaluating and comparing responses from multiple large language models (LLMs) from Open Router to the same inquiry.

## Features

- Sends a single inquiry to four different LLMs
- Each model responds independently
- Responses are saved and displayed per inquiry
- Built using Spring Boot (React frontend in progress)
- Clean separation of concerns between controllers and services

## Contacted Models

The following models are currently queried via OpenRouter:

- agentica-org/deepcoder-14b-preview:free
- deepseek/deepseek-chat-v3-0324:free
- google/gemini-2.5-pro-exp-03-25:free
- qwen/qwq-32b:free

## How to Use

1. Get an API key from [OpenRouter](https://openrouter.ai/)
2. Add your API key in `application.yml`
3. Run the application using your preferred method 

## Status

Backend is functional. Frontend with React and individual model loading states is in development.
