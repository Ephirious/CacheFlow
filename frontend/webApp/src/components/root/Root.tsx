import {useValue, when} from "interop";
import {RootChild, RootComponent} from "k2ts";
import InteropSampleFlowScreen from "../interopSampleFlow/InteropSampleFlow.tsx";
import RootTabBar from "./RootTabBar.tsx";

const RootScreen = ({component}: { component: RootComponent }) => {
    const stack = useValue(component.childStack)
    const activeChild = stack.active;

    return <>
        <div style={{display: "flex", flexDirection: "column", alignItems: "center", gap: "10px"}}>
            <RootTabBar component={component} activeChild={activeChild}/>
            {
                when(activeChild)
                    .on(RootChild.MainChild, (_child) => (
                        <>Main</>
                    ))
                    .on(RootChild.StatsChild, (_child) => (
                        <>Stats</>
                    ))
                    .on(RootChild.SettingsChild, (_child) => (
                        <>Settings</>
                    ))
                    .on(RootChild.InteropSampleFlowChild, (child) => (
                        <InteropSampleFlowScreen component={child.component}/>
                    ))
                    .run()
            }
        </div>
    </>
}

export default RootScreen;
