import {useValue, when} from "interop";
import {RootChild, RootComponent} from "k2ts";
import { motion, AnimatePresence } from "framer-motion";
import InteropSampleFlowScreen from "../../features/interopSampleFlow/InteropSampleFlow.tsx";
import NavBar from "./NavBar.tsx";


const RootScreen = ({component}: { component: RootComponent }) => {
    const stack = useValue(component.childStack)
    const activeChild = stack.active;

    return <>
        <div className={
            "flex flex-col h-screen w-screen" +
            "sm:flex sm:flex-row"
        }>
            <NavBar component={component} activeChild={activeChild}/>

            <AnimatePresence mode="wait">
                <motion.div
                    key={activeChild.constructor.name}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    transition={{ duration: 0.2 }}
                    className="h-full w-full p-8"
                >
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
                </motion.div>
            </AnimatePresence>
        </div>
    </>
}

export default RootScreen;
