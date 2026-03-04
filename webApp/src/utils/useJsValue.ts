import {JsValue} from "k2ts";

import {useState, useEffect} from 'react';

export function useJsValue<T>(jsValue: JsValue<T>): T {
    const [state, setState] = useState<T>(jsValue.value);

    useEffect(() => {
        const disposable = jsValue.subscribe((newValue) => {
            setState(newValue);
        });

        return () => {
            disposable.dispose();
        };
    }, [jsValue]);

    return state;
}