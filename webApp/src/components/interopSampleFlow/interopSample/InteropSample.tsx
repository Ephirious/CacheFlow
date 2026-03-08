import {
    InteropSampleComponent,
    InteropSampleIntent
} from "k2ts";


import styles from './InteropSample.module.css';
import {useValue} from "interop";

const InteropSampleScreen = ({component}: { component: InteropSampleComponent }) => {
    const state = useValue(component.state)
    return <>
        <input className={styles.input}
               type="text"
               value={state.text}
               placeholder="Введите текст..."
               onChange={(e) => {
                   component.intent(new InteropSampleIntent.ChangedText(e.target.value));
               }}
        />

        <h3>На этой вкладке: {state.seconds}</h3>
        <h3>Текст из Kotlin: {state.text}</h3>
    </>
}

export default InteropSampleScreen;