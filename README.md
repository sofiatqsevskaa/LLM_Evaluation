# LLM Evaluation App

This is a simple web application for evaluating and comparing responses from multiple large language models (LLMs) from Open Router to the same inquiry.

## Features

- Send a single inquiry to multiple LLMs
- Choose from any free model available on OpenRouter
- Display and compare model responses per inquiry
- Persist responses for historical reference

## How to Use

1. Get an API key from [OpenRouter](https://openrouter.ai/)
2. Create your `application.yml` in backend/src/main/resources
    ````yaml
    openrouter:
      api:
        key: /*your api key here*/
        url: https://openrouter.ai/api/v1/chat/completions
    ````
3. **Install Dependencies:**

    - **Backend (Spring Boot)**:
        - Ensure you have **Java 17+** installed on your machine.
        - Install **Maven** or **Gradle** to manage project dependencies (usually Maven for Spring Boot projects).
        - Run the following command in the backend directory to download required dependencies:
          ````bash
          mvn install
          ````

    - **Frontend (React)**:
        - Ensure you have **Node.js** (version 16 or higher) and **npm** installed on your machine.
        - In the frontend directory, install the necessary dependencies:
          ````bash
          npm install
          ````

4. **Run the Backend:**
    - Navigate to the `backend` directory and run:
      ```bash
      mvn spring-boot:run
      ```
5. **Run the Frontend:**
    - Navigate to the `frontend` directory and run:
      ```bash
      npm start
      ```
6. **Use the App:** 
    - Running the frontend will automatically open the app on your default browser.
   - You can now test the evaluation of responses from the different LLMs.

