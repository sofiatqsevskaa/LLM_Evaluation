import React, {useState, useEffect, FC, ChangeEvent} from "react";
import axios from "axios";
import 'bootstrap/dist/css/bootstrap.min.css';


interface ModelSelectionPageProps {
    onModelsSelected: (models: string[]) => void;
}

const ModelSelectionPage: FC<ModelSelectionPageProps> = ({onModelsSelected}) => {
    const [models, setModels] = useState<string[]>([]);
    const [selectedModels, setSelectedModels] = useState<string[]>([]);

    useEffect(() => {
        axios.get("http://localhost:9091/inquiry/models").then((response) => {
            setModels(response.data);
        });
    }, []);

    const handleCheckbox = (event: React.ChangeEvent<HTMLInputElement>) => {
        const selectedModel = event.target.name;
        if (selectedModels.includes(selectedModel)) {
            setSelectedModels(selectedModels.filter(model => model !== selectedModel));
        } else {
            setSelectedModels([...selectedModels, selectedModel]);
        }
    }

    const handleStart = () => {
        onModelsSelected(selectedModels);
    };

    const modelCards = models.map((model: string, index: number) => {
        return (
            <div key={index} className="col-md-4 col-sm-12 mb-4">
                <div className="card h-100">
                    <div className="card-body">
                        <h5 className="card-title">{model}</h5>
                        <div className="form-check">
                            <input className="form-check-input" type="checkbox" name={model} id={`checkbox-${model}`}
                                   checked={selectedModels.includes(model)} onChange={handleCheckbox}/>
                            <label className="form-check-label" htmlFor={`checkbox-${model}`}>Select this model</label>
                        </div>
                    </div>
                </div>
            </div>
        );
    });

    return (
        <div className="container">
            <h1 className="h1 text-center text-primary mb-4">Choose Your Models</h1>
            <div className="row">
                {modelCards}
            </div>
            <button
                className="btn btn-primary mt-3"
                onClick={() => onModelsSelected(selectedModels)}
            >
                Submit Selected Models
            </button>
        </div>
    );

    // return (
    //     <div>
    //         <h1>Select Models</h1>
    //
    //         {models.map((model) =>
    //             <div key={model}>
    //                 <label>
    //                     <input
    //                         type="checkbox"
    //                         checked={selectedModels.includes(model)}
    //                         onChange={(e) => handleCheckbox(model, e)}
    //                     />
    //                     {model}
    //                 </label>
    //             </div>
    //         )}
    //
    //         <button onClick={handleStart}>
    //             Start Chat
    //         </button>
    //     </div>
    // );
}

export default ModelSelectionPage;
