import {useValue, when} from "interop";
import {RootChild, RootComponent} from "k2ts";
import {motion, AnimatePresence} from "framer-motion";
import {useLayoutEffect} from "react";
import NavBar from "./NavBar.tsx";
import Main from "./Main.tsx";
import Settings from "./Settings.tsx";
import Stats from "./Stats.tsx";


const RootScreen = ({component}: { component: RootComponent }) => {
    const pages = useValue(component.childPages)
    const activeChild = pages.active;
    const isMainActive = activeChild instanceof RootChild.MainChild;
    const disablePageOverscroll = activeChild instanceof RootChild.StatsChild || activeChild instanceof RootChild.SettingsChild;

    useLayoutEffect(() => {
        const themeMeta = document.head.querySelector<HTMLMetaElement>('meta[name="theme-color"]');
        const appleStatusMeta = document.head.querySelector<HTMLMetaElement>('meta[name="apple-mobile-web-app-status-bar-style"]');

        const color = isMainActive ? "#4F39F6" : "#ffffff";
        if (themeMeta) themeMeta.content = color;

        document.documentElement.style.backgroundColor = color;
        document.body.style.backgroundColor = color;

        if (appleStatusMeta) {
            appleStatusMeta.content = isMainActive ? "black-translucent" : "default";
        }
    }, [isMainActive]);

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
                    style={{overscrollBehaviorY: disablePageOverscroll ? "none" : "auto"}}
                >
                    {when(activeChild)
                        .on(RootChild.MainChild, (child) => (
                            <Main component={child.component}/>
                        ))
                        .on(RootChild.StatsChild, (child) => (
                            <Stats component={child.component}/>
                        ))
                        .on(RootChild.SettingsChild, (child) => <Settings component={child.component}/>)
                        .run()}
                </motion.div>
            </AnimatePresence>


            <NavBar component={component} activeChild={activeChild}/>

        </div>
    )
}

export default RootScreen;
