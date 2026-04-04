import {useValue, when} from "interop";
import {RootChild, RootComponent} from "k2ts";
import {motion, AnimatePresence} from "framer-motion";
import InteropSampleFlowScreen from "../../features/interopSampleFlow/InteropSampleFlow.tsx";
import NavBar from "./NavBar.tsx";
import Main from "./Main.tsx";


const RootScreen = ({component}: { component: RootComponent }) => {
    const stack = useValue(component.childStack)
    const activeChild = stack.active;

    return (
        <div
            className={
                "flex flex-col h-dvh w-screen sm:flex sm:flex-row fixed"
            }
        >
            <AnimatePresence mode="wait">
                <motion.div
                    key={activeChild.constructor.name}
                    initial={{opacity: 0, y: 0}}
                    animate={{opacity: 1, y: 0}}
                    exit={{opacity: 0, y: 0}}
                    transition={{duration: 0.2}}
                    className="relative z-0 flex-1 min-h-0 h-screen w-full overflow-y-auto no-scrollbar"
                >
                    {when(activeChild)
                        .on(RootChild.MainChild, (child) => (
                            <Main component={child.component}/>
                        ))
                        .on(RootChild.StatsChild, (_child) => <>Stats</>)
                        .on(RootChild.SettingsChild, (_child) => <>Settings</>)
                        .on(RootChild.InteropSampleFlowChild, (child) => (
                            <InteropSampleFlowScreen component={child.component}/>
                        ))
                        .run()}
                </motion.div>
            </AnimatePresence>


            <NavBar component={component} activeChild={activeChild}/>

        </div>
    )
}

export default RootScreen;
