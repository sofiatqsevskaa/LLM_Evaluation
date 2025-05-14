import { useState, useCallback } from "react";
import ModelSelectionPage from "./ModelSelectionPage";
import ChatComponent from "./ChatComponent";

function App() {
    const [selectedModels, setSelectedModels] = useState<string[]>([]);

    const handleModelsSelected = useCallback((models: string[]) => {
        setSelectedModels(models);
    }, []);

    if (selectedModels.length == 0) {
        return <ModelSelectionPage onModelsSelected={handleModelsSelected} />;
    } else {
        return <ChatComponent models={selectedModels}/>;
    }
}

export default App;