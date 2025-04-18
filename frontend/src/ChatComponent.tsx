import React, { useEffect, useState } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';

interface Answer {
    model: string;
    answer: string;
}

const ChatComponent: React.FC = () => {
    const [models, setModels] = useState<string[]>([]);
    const [answers, setAnswers] = useState<Record<string, string>>({});
    const [loadingModels, setLoadingModels] = useState<Set<string>>(new Set());
    const [inquiryContent, setInquiryContent] = useState("");

    useEffect(() => {
        fetch("http://localhost:9091/inquiry/models")
            .then((res) => res.json())
            .then((data) => setModels(data))
            .catch((err) => console.error("Failed to fetch models:", err));
    }, []);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setAnswers({});
        const newLoading = new Set(models);
        setLoadingModels(newLoading);
        models.forEach((model) => sendInquiryToModel(model));
    };

    const sendInquiryToModel = (model: string) => {
        const eventSource = new EventSource(`http://localhost:9091/inquiry/stream?inquiryContent=${encodeURIComponent(inquiryContent)}&model=${encodeURIComponent(model)}`);

        eventSource.onmessage = (event) => {
            const data: Answer = JSON.parse(event.data);
            setAnswers((prev) => ({ ...prev, [data.model]: data.answer }));
            setLoadingModels((prev) => {
                const updated = new Set(prev);
                updated.delete(data.model);
                return updated;
            });
            eventSource.close();
        };

        eventSource.onerror = (err) => {
            console.error("EventSource error for model", model, ":", err);
            setLoadingModels((prev) => {
                const updated = new Set(prev);
                updated.delete(model);
                return updated;
            });
            setAnswers((prev) => ({ ...prev, [model]: "Failed to load response." }));
            eventSource.close();
        };
    };

    const retryModel = (model: string) => {
        setAnswers((prev) => ({ ...prev, [model]: "" }));
        setLoadingModels((prev) => new Set(prev).add(model));
        sendInquiryToModel(model);
    };

    return (
        <div className="container py-5">
            <h1 className="h1 text-center text-primary mb-4">LLM Evaluation Tool</h1>

            <form onSubmit={handleSubmit} className="bg-light p-4 rounded shadow-sm mb-5">
                <div className="form-group mb-3">
          <textarea
              className="form-control"
              rows={4}
              placeholder="Enter your prompt here..."
              value={inquiryContent}
              onChange={(e) => setInquiryContent(e.target.value)}
          ></textarea>
                </div>
                <button type="submit" className="btn btn-primary">
                    Send Inquiry
                </button>
            </form>

            <div className="row">
                {models.map((model) => (
                    <div className="col-md-6 mb-4" key={model}>
                        <div className="card h-100">
                            <div className="card-body">
                                <h5 className="card-title text-secondary">{model}</h5>
                                {loadingModels.has(model) ? (
                                    <div className="d-flex align-items-center">
                                        <div className="spinner-border text-primary me-2" role="status">
                                            <span className="visually-hidden">Loading...</span>
                                        </div>
                                        <span>Loading...</span>
                                    </div>
                                ) : (
                                    <>
                                        <p className="card-text white-space-pre-line">{answers[model]}</p>
                                        <button
                                            className="btn btn-outline-primary btn-sm mt-2"
                                            onClick={() => retryModel(model)}
                                        >
                                            Retry
                                        </button>
                                    </>
                                )}
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ChatComponent;
