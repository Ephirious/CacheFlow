import {JSLogo} from "../JSLogo/JSLogo";
import {match} from "../../utils/matcher.ts";
import {useJsValue} from "../../utils/useJsValue.ts";
import {initRealRootComponent, InteropTestComponent, InteropTestIntent, InteropTestState, RootChild} from "k2ts";
import {useMemo, useState} from "react";

export function Greeting() {
    const root = useMemo(() => initRealRootComponent(), []);
    const [isVisible, setIsVisible] = useState<boolean>(false);
    const [isAnimating, setIsAnimating] = useState<boolean>(false);

    const stack = useJsValue(root.jsStack);
    const activeChild = stack.active;

    const handleAnimationEnd = (event: React.AnimationEvent<HTMLDivElement>) => {
        if (event.animationName === 'fadeOut') {
            setIsVisible(false);
            setIsAnimating(false);
        }
    };

    return (
        <div className="greeting-container">
            <button onClick={() => isVisible ? setIsAnimating(true) : setIsVisible(true)}>
                {isVisible ? 'Hide' : 'Show'}
            </button>

            {isVisible && (
                <div
                    className={isAnimating ? 'greeting-content fade-out' : 'greeting-content'}
                    onAnimationEnd={handleAnimationEnd}
                >
                    <JSLogo/>

                    {/* Безопасное условие: рендерим компонент, а не вызываем хук напрямую */}
                    {activeChild instanceof RootChild.InteropTestChild && (
                        <InteropTestView component={activeChild.component}/>
                    )}
                </div>
            )}
        </div>
    );
}

const InteropTestView = ({ component }: { component: InteropTestComponent }) => {
    const state = useJsValue(component.jsState);

    return match(state)
        .is(InteropTestState.Loading, () => (
            <div>Загрузка из Kotlin...</div>
        ))
        .on(InteropTestState.OK, (s) => (
            <div className="container" style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                <h1>Данные из Kotlin: {s.text}</h1>

                {/* Текстовое поле для ввода */}
                <input
                    type="text"
                    value={s.text}
                    placeholder="Введите текст..."
                    style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                    onChange={(e) => {
                        // Отправляем интент в Kotlin при каждом изменении
                        component.intent(new InteropTestIntent.ChangedText(e.target.value));
                    }}
                />

                <button onClick={() => component.restartState()}>
                    Сбросить состояние
                </button>
            </div>
        ))
        .on(InteropTestState.Error, (s) => (
            <div style={{ color: 'red' }}>Ошибка: {s.error}</div>
        ))
        .otherwise(() => <div>Что-то пошло не так</div>);
};