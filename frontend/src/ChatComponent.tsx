import React, { useState, useRef, useEffect } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import ReactMarkdown from 'react-markdown';

interface Answer {
    model: string;
    answer: string;
    inquiryId?: string;
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
    const [inquiryId, setInquiryId] = useState<string | undefined>();
    const fileInputRef = useRef<HTMLInputElement>(null);
    const checkTimeoutsRef = useRef<Record<string, NodeJS.Timeout>>({});

    const checkDatabaseForAnswer = async (model: string) => {
        if (!inquiryId) return;

        try {
            const response = await fetch(
                `http://localhost:9091/inquiry/answer?inquiryId=${inquiryId}&model=${model}`
            );
            
            if (response.ok) {
                const data = await response.json();
                setAnswers(prev => ({...prev, [model]: data.answer}));
                setStatusMap(prev => ({...prev, [model]: 'success'}));
                if (checkTimeoutsRef.current[model]) {
                    clearTimeout(checkTimeoutsRef.current[model]);
                    delete checkTimeoutsRef.current[model];
                }
            } else {
                if (statusMap[model] === 'loading') {
                    checkTimeoutsRef.current[model] = setTimeout(() => checkDatabaseForAnswer(model), 3000);
                }
            }
        } catch (error) {
            console.error(`Error checking answer for model ${model}:`, error);
        }
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setAnswers({});
        setStatusMap(Object.fromEntries(models.map((model) => [model, 'loading'])));
        setInquiryId(undefined);

        Object.values(checkTimeoutsRef.current).forEach(timeout => clearTimeout(timeout));
        checkTimeoutsRef.current = {};

        if (selectedFile) {
            sendImageToModel(models[0]);
            models.slice(1).forEach((model) => {
                setTimeout(() => sendImageToModel(model), 100);
            });
        } else {
            sendInquiryToModel(models[0]);
            models.slice(1).forEach((model) => {
                setTimeout(() => sendInquiryToModel(model), 100);
            });
        }
    };

    const sendInquiryToModel = (model: string) => {
        const eventSource = new EventSource(
            `http://localhost:9091/inquiry/stream/single?inquiryContent=${encodeURIComponent(inquiryContent)}&model=${encodeURIComponent(model)}${inquiryId ? `&inquiryId=${inquiryId}` : ''}`
        );

        eventSource.onmessage = (event) => {
            const data: Answer = JSON.parse(event.data);
            setAnswers((prev) => ({...prev, [data.model]: data.answer}));
            setStatusMap((prev) => ({...prev, [data.model]: 'success'}));
            if (data.inquiryId) {
                setInquiryId(data.inquiryId);
            }
            eventSource.close();
        };

        eventSource.onerror = (err) => {
            console.error("EventSource error for model", model, ":", err);
            setAnswers((prev) => ({...prev, [model]: "Failed to load response."}));
            setStatusMap((prev) => ({...prev, [model]: 'error'}));
            eventSource.close();

            if (inquiryId) {
                checkTimeoutsRef.current[model] = setTimeout(() => checkDatabaseForAnswer(model), 3000);
            }
        };
    };

    useEffect(() => {
        return () => {
            Object.values(checkTimeoutsRef.current).forEach(timeout => clearTimeout(timeout));
        };
    }, []);
    
    const retryModel = (model: string) => {
        setAnswers((prev) => ({...prev, [model]: ""}));
        setStatusMap((prev) => ({...prev, [model]: 'loading'}));
        
        if (checkTimeoutsRef.current[model]) {
            clearTimeout(checkTimeoutsRef.current[model]);
            delete checkTimeoutsRef.current[model];
        }

        if (selectedFile) {
            sendImageToModel(model);
        } else {
            sendInquiryToModel(model);
        }
    };

    const sendImageToModel = (model: string) => {
        if (!selectedFile) return;

        const reader = new FileReader();
        reader.onload = async (e) => {
            const base64Image = e.target?.result as string;

            const eventSource = new EventSource(`http://localhost:9091/inquiry/stream/single/image`, {
                withCredentials: false
            });

            // Send the POST request separately
            fetch(`http://localhost:9091/inquiry/stream/single/image`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "text/event-stream",
                },
                credentials: 'include',
                body: JSON.stringify({
                    model: model,
                    image: base64Image,
                    prompt: inquiryContent || "What's in this image?",
                    inquiryId: inquiryId
                }),
            }).catch(error => {
                console.error("Error sending image:", error);
                setAnswers(prev => ({...prev, [model]: "Failed to send image."}));
                setStatusMap(prev => ({...prev, [model]: 'error'}));
                eventSource.close();
            });

            eventSource.onmessage = (event) => {
                try {
                    const data = JSON.parse(event.data);
                    setAnswers(prev => ({...prev, [data.model]: data.answer}));
                    setStatusMap(prev => ({...prev, [data.model]: 'success'}));
                    if (data.inquiry?.id) {
                        setInquiryId(data.inquiry.id);
                    }
                    eventSource.close();
                } catch (e) {
                    console.error("Invalid SSE data:", e);
                    setAnswers(prev => ({...prev, [model]: "Failed to parse response."}));
                    setStatusMap(prev => ({...prev, [model]: 'error'}));
                    eventSource.close();
                }
            };

            eventSource.onerror = () => {
                console.error("EventSource error for model", model);
                setAnswers(prev => ({...prev, [model]: "Failed to load response."}));
                setStatusMap(prev => ({...prev, [model]: 'error'}));
                eventSource.close();

                if (inquiryId) {
                    checkTimeoutsRef.current[model] = setTimeout(() => checkDatabaseForAnswer(model), 3000);
                }
            };
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