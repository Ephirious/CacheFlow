import { useEffect, useRef } from "react";

export function useActions<A>(
    component: { subscribeActions: (cb: (action: A) => void) => void },
    handler: (action: A) => void
) {
    const handlerRef = useRef(handler);
    handlerRef.current = handler;

    useEffect(() => {
        component.subscribeActions((action) => {
            handlerRef.current(action);
        });
    }, [component]);
}