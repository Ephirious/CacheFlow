import React from 'react';
import ReactDOM from 'react-dom/client';
import {initKoinJS, initRealRootComponent} from "k2ts";
import RootScreen from "./components/root/Root.tsx";

initKoinJS();
const rootComponent = initRealRootComponent()

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

ReactDOM.createRoot(rootElement).render(
    <React.StrictMode>
        <RootScreen component={rootComponent}/>
    </React.StrictMode>
);