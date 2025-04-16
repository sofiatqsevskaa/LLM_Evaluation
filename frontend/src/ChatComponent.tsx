import React, { useState } from "react";

interface Answer {
    model: string;
    answer: string;
}

const ChatComponent: React.FC = () => {
    const [inquiryContent, setInquiryContent] = useState("");
    const [answers, setAnswers] = useState<Answer[]>([]);
    const [loadingModels, setLoadingModels] = useState<string[]>([]);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setAnswers([]);
        const eventSource = new EventSource(`http://localhost:9091/inquiry/stream?inquiryContent=${encodeURIComponent(inquiryContent)}`);

        eventSource.onmessage = (event) => {
            const data: Answer = JSON.parse(event.data);
            setAnswers(prev => [...prev, data]);
            setLoadingModels(prev => prev.filter(model => model !== data.model));
        };

        eventSource.onerror = (error) => {
            console.error("Streaming error:", error);
            eventSource.close();
        };

        setLoadingModels(["agentica-org/deepcoder-14b-preview:free", "deepseek/deepseek-chat-v3-0324:free", "google/gemini-2.5-pro-exp-03-25:free"]);
    };

    return (
        <div>
            <form onSubmit={handleSubmit}>
        <textarea
            value={inquiryContent}
            onChange={(e) => setInquiryContent(e.target.value)}
        />
                <button type="submit">Send</button>
            </form>

            {loadingModels.map(model => (
                <div key={model}>Loading {model}...</div>
            ))}

            <ul>
                {answers.map((ans, i) => (
                    <li key={i}>
                        <strong>{ans.model}</strong>: {ans.answer}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default ChatComponent;
