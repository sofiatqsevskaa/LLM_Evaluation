import React, { useState, useRef} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import ReactMarkdown from 'react-markdown';


interface Answer {
    model: string;
    answer: string;
}

interface ChatComponentProps {
    models: string[];
    onChangeModels: () => void;
}

const ChatComponent: React.FC<ChatComponentProps> = ({models, onChangeModels}) => {
    const [answers, setAnswers] = useState<Record<string, string>>({});
    const [statusMap, setStatusMap] = useState<Record<string, 'loading' | 'success' | 'error'>>({});
    const [inquiryContent, setInquiryContent] = useState("");
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const fileInputRef = useRef<HTMLInputElement>(null);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setAnswers({});
        setStatusMap(Object.fromEntries(models.map((model) => [model, 'loading'])));

        if (selectedFile) {
            models.forEach((model) => sendImageToModel(model));
        } else {
            models.forEach((model) => sendInquiryToModel(model));
        }
    };

    const sendImageToModel = (model: string) => {
        if (!selectedFile) return;

        const reader = new FileReader();
        reader.onload = async (e) => {
            const base64Image = e.target?.result as string;

            const response = await fetch(`http://localhost:9091/inquiry/stream/image`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    model: model,
                    image: base64Image,
                    prompt: inquiryContent || "What's in this image?",
                }),
            });

            const reader = response.body?.getReader();
            const decoder = new TextDecoder("utf-8");
            let result = "";

            while (true) {
                const {value, done} = await reader!.read();
                if (done) break;

                result += decoder.decode(value, {stream: true});

                const events = result.split("\n\n");
                for (const evt of events) {
                    if (evt.startsWith("data:")) {
                        try {
                            const json = JSON.parse(evt.replace("data:", "").trim());
                            setAnswers((prev) => ({...prev, [json.model]: json.answer}));
                            setStatusMap((prev) => ({...prev, [json.model]: 'success'}));
                        } catch (e) {
                            console.error("Invalid SSE data:", e);
                            setAnswers((prev) => ({...prev, [model]: "Failed to parse response."}));
                            setStatusMap((prev) => ({...prev, [model]: 'error'}));
                        }
                    }
                }
            }
        };

        reader.readAsDataURL(selectedFile);
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            setSelectedFile(e.target.files[0]);
        }
    };

    const clearFile = () => {
        setSelectedFile(null);
        if (fileInputRef.current) {
            fileInputRef.current.value = '';
        }
    };

    const sendInquiryToModel = (model: string) => {
        const eventSource = new EventSource(`http://localhost:9091/inquiry/stream?inquiryContent=${encodeURIComponent(inquiryContent)}&model=${encodeURIComponent(model)}`);

        eventSource.onmessage = (event) => {
            const data: Answer = JSON.parse(event.data);
            setAnswers((prev) => ({...prev, [data.model]: data.answer}));
            setStatusMap((prev) => ({...prev, [data.model]: 'success'}));
            eventSource.close();
        };

        eventSource.onerror = (err) => {
            console.error("EventSource error for model", model, ":", err);
            setAnswers((prev) => ({...prev, [model]: "Failed to load response."}));
            setStatusMap((prev) => ({...prev, [model]: 'error'}));
            eventSource.close();
        };
    };

    const retryModel = (model: string) => {
        setAnswers((prev) => ({...prev, [model]: ""}));
        setStatusMap((prev) => ({...prev, [model]: 'loading'}));
        if (selectedFile) {
            sendImageToModel(model);
        } else {
            sendInquiryToModel(model);
        }
    };

    return (
        <div className="container py-5">
            <h1 className="h1 text-center text-primary mb-4">LLM Evaluation Tool</h1>

            <form onSubmit={handleSubmit} className="bg-light p-4 rounded shadow-sm mb-5">
                <div className="form-group mb-3">
                    <textarea
                        className="form-control mb-3"
                        rows={4}
                        placeholder="Enter your prompt here..."
                        value={inquiryContent}
                        onChange={(e) => setInquiryContent(e.target.value)}
                    ></textarea>

                    <div className="mb-3">
                        <input
                            type="file"
                            className="form-control"
                            accept="image/*"
                            onChange={handleFileChange}
                            ref={fileInputRef}
                        />
                        {selectedFile && (
                            <button
                                type="button"
                                className="btn btn-secondary btn-sm mt-2"
                                onClick={clearFile}
                            >
                                Clear Image
                            </button>
                        )}
                    </div>
                </div>
                <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={!inquiryContent && !selectedFile}
                >
                    Send Inquiry
                </button>
            </form>

            <div className="form-group mt-3 d-flex justify-content-end">
                <button className="btn btn-primary" onClick={onChangeModels}>
                    Change Models
                </button>
            </div>

            <div className="row">
                {models.map((model) => (
                    <div className="col-md-6 mb-4" key={model}>
                        <div className="card h-100">
                            <div className="card-body">
                                <h5 className="card-title text-secondary">{model}</h5>
                                {statusMap[model] === 'loading' ? (
                                    <div className="d-flex align-items-center">
                                        <div className="spinner-border text-primary me-2" role="status">
                                            <span className="visually-hidden">Loading...</span>
                                        </div>
                                        <span>Waiting for response...</span>
                                    </div>
                                ) : statusMap[model] === 'error' ? (
                                    <>
                                        <p className="card-text text-danger">{answers[model]}</p>
                                        <button
                                            className="btn btn-outline-primary btn-sm mt-2"
                                            onClick={() => retryModel(model)}
                                        >
                                            Retry
                                        </button>
                                    </>
                                ) : (
                                    <>
                                        <p className="card-text white-space-pre-line">
                                            <ReactMarkdown>
                                                {answers[model]}
                                            </ReactMarkdown>
                                        </p>
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
