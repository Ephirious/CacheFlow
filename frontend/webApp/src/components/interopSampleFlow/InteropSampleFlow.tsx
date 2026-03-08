import {
    InteropSampleFlowChild,
    InteropSampleFlowComponent,
    InteropSampleFlowIntent,
    InteropSampleFlowState
} from "k2ts";
import {useValue, when} from "interop";
import InteropSampleScreen from "./interopSample/InteropSample.tsx";
import styles from './InteropSampleFlow.module.css';
import shit from './interopSample/InteropSample.module.css';

const InteropSampleFlowScreen = ({component}: { component: InteropSampleFlowComponent }) => {
    const state = useValue(component.state);

    const stack = useValue(component.childStack);
    const activeChild = stack.active;

    const items: InteropSampleFlowChild.InteropSampleChild[] = [...stack.backStack.asJsReadonlyArrayView(), activeChild].filter(
        (child): child is InteropSampleFlowChild.InteropSampleChild =>
            child instanceof InteropSampleFlowChild.InteropSampleChild
    ).sort((a, b) => a.component.num - b.component.num);
    return (
        <div className={styles.container}>
            <div>
                <h2>InteropSampleFlowScreen</h2>

                <div className={styles.weatherBox}>
                    <button className={styles.button}
                            onClick={() => {
                                component.intent(InteropSampleFlowIntent.ClickedRefresh)
                            }}
                    >
                        Обновить
                    </button>
                    {when(state.weatherState)
                        .is(InteropSampleFlowState.WeatherState.Loading, () => (
                            "Loading..."
                        ))
                        .on(InteropSampleFlowState.WeatherState.OK, (ok) => (
                            "Погода в Москве: " + ok.weather.temperature + ok.weather.temperatureUnit
                        ))
                        .on(InteropSampleFlowState.WeatherState.Error, (error) => (
                            error.error
                        ))
                        .run()
                    }
                </div>
                <input className={shit.input}

                       style={{marginBottom:'10px'}}
                       type="text"
                       value={state.sampleText}
                       placeholder="Сохраняется в localStorage"
                       onChange={(e) => {
                           component.intent(new InteropSampleFlowIntent.ChangedSampleText(e.target.value));
                       }}
                />

            </div>
            <button className={styles.button}
                    style={{marginRight:'5px'}}
                    onClick={() => {
                        component.createNewTab()
                    }}
            >
                Add new
            </button>
            <button className={styles.button}
                    onClick={() => {
                        component.onBackClicked()
                    }}
            >
                Delete current
            </button>
            <div className={styles.box}>
                <div className={styles.tabs}>
                    {items.map((item, _) => (
                        <button
                            key={item.component.num}
                            onClick={() => {
                                component.navigateToTab(item.component.num)
                            }}
                            className={`${styles.button} ${item === activeChild ? styles.active : ''}`}
                        >
                            Screen {item.component.num}
                        </button>
                    ))}
                </div>

                <div>
                    {when(activeChild)
                        .on(InteropSampleFlowChild.InteropSampleChild, (child) => (
                            <InteropSampleScreen component={child.component}/>
                        ))
                        .run()}
                </div>
            </div>
        </div>
    );
};

export default InteropSampleFlowScreen;
