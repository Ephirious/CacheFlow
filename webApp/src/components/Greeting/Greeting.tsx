import {useValue, when} from "../../interop";
import {initRealRootComponent, InteropTestComponent, InteropTestIntent, InteropTestState, RootChild} from "k2ts";
import {useMemo} from "react";


export function Greeting() {
    const root = useMemo(() => initRealRootComponent(), []);

    const stack = useValue(root.childStack);
    const activeChild = stack.active;


    return (
        <div>
            <>{stack.backStack.map((child, index) => (
                <li key={index} style={{color: 'gray'}}>
                    [{index}] {child.constructor.name}
                </li>
            ))}</>
            <button onClick={() => root.testPush()}>
                {"New screen"}
            </button>
            <button onClick={() => root.onBackClicked()}>
                {"Pop"}
            </button>

            {activeChild instanceof RootChild.InteropTestChild && (
                <InteropTestView component={activeChild.component}/>
            )}
        </div>
    );
}

const InteropTestView = ({component}: { component: InteropTestComponent }) => {
    const state = useValue(component.state);

    const styles = `
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        @keyframes pulse {
            0% { opacity: 0.5; }
            50% { opacity: 1; }
            100% { opacity: 0.5; }
        }
        .animate-fade-in {
            animation: fadeIn 1.4s ease-out forwards;
        }
        .animate-pulse {
            animation: pulse 2.5s infinite ease-in-out;
        }
    `;

    return <>
        <style>{styles}</style>
        {
            when(state)
                .is(InteropTestState.Loading, () => (
                    <div>Загрузка из Kotlin...</div>
                ))
                .on(InteropTestState.OK, (s) => (
                    <div className="container" style={{display: 'flex', flexDirection: 'column', gap: '10px'}}>
                        <h1>Данные из Kotlin: {s.text}</h1>
                        <input
                            type="text"
                            value={s.text}
                            placeholder="Введите текст..."
                            style={{padding: '8px', borderRadius: '4px', border: '1px solid #ccc'}}
                            onChange={(e) => {
                                component.intent(new InteropTestIntent.ChangedText(e.target.value));
                            }}
                        />

                        <button onClick={() => component.restartState()}>
                            Сбросить состояние
                        </button>
                    </div>
                ))
                .on(InteropTestState.Error, (s) => (
                    <div style={{color: 'red'}}>Ошибка: {s.error}</div>
                ))
                .otherwise(() => <div>Что-то пошло не так</div>)
        }
        {<div key={component.num} className="animate-pulse" style={{color: '#666', fontWeight: 'bold'}}>
            Some animation: {component.num}
        </div>}
    </>
};