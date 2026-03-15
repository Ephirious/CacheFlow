import React from 'react';
import ReactDOM from 'react-dom/client';
import RootScreen from "./components/root/Root.tsx";
import {initApp} from "k2ts";


const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

(async () => {
    const rootComponent = await initApp();

    ReactDOM.createRoot(rootElement).render(
        <React.StrictMode>
            <RootScreen component={rootComponent}/>
        </React.StrictMode>
    );
})();