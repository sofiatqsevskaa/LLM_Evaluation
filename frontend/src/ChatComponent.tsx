import React, {useEffect, useState} from "react";

interface Answer {
    model: string;
    answer: string;
}

const ChatComponent: React.FC = () => {
    const [models, setModels] = useState<string[]>([]);
    const [inquiryContent, setInquiryContent] = useState("");
    const [answers, setAnswers] = useState<Answer[]>([]);
    const [loadingModels, setLoadingModels] = useState<string[]>([]);

    useEffect(() => {
        fetch("http://localhost:9091/models")
            .then((res) => res.json())
            .then((data) => {console.log(data);setModels(data)})
            .catch((err) => console.error("Failed to fetch models:", err));
    }, []);

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

        setLoadingModels([...models]);
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
