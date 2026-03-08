import {useValue, when} from "interop";
import {RootChild, RootComponent} from "k2ts";
import InteropSampleFlowScreen from "../interopSampleFlow/InteropSampleFlow.tsx";

const RootScreen = ({component}: { component: RootComponent }) => {
    const stack = useValue(component.childStack)
    const activeChild = stack.active;

    return <>
        {
            when(activeChild)
                .on(RootChild.InteropSampleFlowChild, (child) => (
                    <InteropSampleFlowScreen component={child.component}/>
                ))
                .run()
        }
    </>
}

export default RootScreen;
