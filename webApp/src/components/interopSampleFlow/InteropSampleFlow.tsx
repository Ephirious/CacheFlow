import {
    InteropSampleFlowChild,
    InteropSampleFlowComponent,
    InteropSampleFlowIntent,
    InteropSampleFlowState
} from "k2ts";
import {useValue, when} from "interop";
import InteropSampleScreen from "./interopSample/InteropSample.tsx";
import styles from './InteropSampleFlow.module.css';

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
                    {when(state)
                        .is(InteropSampleFlowState.Loading, () => (
                            "Loading..."
                        ))
                        .on(InteropSampleFlowState.OK, (ok) => (
                            "Погода в Москве: " + ok.weather.temperature + ok.weather.temperatureUnit
                        ))
                        .on(InteropSampleFlowState.Error, (error) => (
                            error.error
                        ))
                        .run()
                    }
                </div>

            </div>
            <button className={styles.button}
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
