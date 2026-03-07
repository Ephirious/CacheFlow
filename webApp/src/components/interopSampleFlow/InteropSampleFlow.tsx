import {
    InteropSampleFlowChild,
    InteropSampleFlowComponent
} from "k2ts";
import {useValue, when} from "interop";
import InteropSampleScreen from "./interopSample/InteropSample.tsx";
import styles from './InteropSampleFlow.module.css';

const InteropSampleFlowScreen = ({component}: { component: InteropSampleFlowComponent }) => {
    const stack = useValue(component.childStack);
    const activeChild = stack.active;

    const items: InteropSampleFlowChild.InteropSampleChild[] = [...stack.backStack.asJsReadonlyArrayView(), activeChild].filter(
        (child): child is InteropSampleFlowChild.InteropSampleChild =>
            child instanceof InteropSampleFlowChild.InteropSampleChild
    ).sort((a, b) => a.component.num - b.component.num);
    return (
        <div className={styles.container}>
            <h2>InteropSampleFlowScreen</h2>
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
