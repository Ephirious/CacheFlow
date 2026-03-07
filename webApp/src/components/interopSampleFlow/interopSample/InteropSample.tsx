import {
    InteropSampleComponent,
    InteropSampleIntent
} from "k2ts";
import {useValue} from "interop";

const InteropSampleScreen = ({component}: { component: InteropSampleComponent }) => {
    const state = useValue(component.state)
    return <>
        <input
            type="text"
            value={state.text}
            placeholder="Введите текст..."
            style={{padding: '8px', borderRadius: '4px', border: '1px solid #ccc'}}
            onChange={(e) => {
                component.intent(new InteropSampleIntent.ChangedText(e.target.value));
            }}
        />

        <h3>На этой вкладке: {state.seconds}</h3>
        <h3>Текст из Kotlin: {state.text}</h3>
    </>
}

export default InteropSampleScreen;