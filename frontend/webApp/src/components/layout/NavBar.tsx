import {RootChild, RootComponent, RootOutput} from "k2ts";
import {LuWallet} from "react-icons/lu";
import {FiHome} from "react-icons/fi";
import {GrLineChart} from "react-icons/gr";
import {LuSettings} from "react-icons/lu";
import {LuSquareActivity} from "react-icons/lu";
import {motion} from "framer-motion";

interface RootTabBarProps {
    component: RootComponent;
    activeChild: RootChild
}


const NavBar = ({component, activeChild}: RootTabBarProps) => {
    const btnBase = "relative flex flex-col items-center justify-center rounded-2xl text-base font-semibold transition-colors duration-300 outline-none " +
        "md:flex-row md:justify-start md:px-4 md:py-3 md:gap-4 md:w-full";

    const btnActive = "text-brand-primary";
    const btnInActive = "text-text-nav hover:text-text-nav-hover";

    const menuItems = [
        {
            label: "Главная",
            icon: <FiHome className="w-6 h-6 stroke-2"/>,
            child: RootChild.MainChild,
            output: RootOutput.NavigateToMain
        },
        {
            label: "Статистика",
            icon: <GrLineChart className={"w-6 h-6 stroke-2"}/>,
            child: RootChild.StatsChild,
            output: RootOutput.NavigateToStats
        },
        {
            label: "Настройки",
            icon: <LuSettings className={"w-6 h-6 stroke-2"}/>,
            child: RootChild.SettingsChild,
            output: RootOutput.NavigateToSettings
        },
        {
            label: "Interop",
            icon: <LuSquareActivity/>,
            child: RootChild.InteropSampleFlowChild,
            output: RootOutput.NavigateToInteropTest
        }
    ];

    return (
        <nav className="
            relative z-50 shrink-0
            bottom-0 left-0 right-0
            bg-surface-base border-t border-border-contrast
            pb-[env(safe-area-inset-bottom)]
            md:relative md:flex md:h-full md:w-96 md:flex-col md:border-r md:border-t-0 md:p-6 md:bg-transparent
        ">
            <h1 className="hidden md:flex items-center gap-3 font-bold text-2xl mb-8">
                <LuWallet className="w-12 h-12 p-2.5 bg-surface-nav-active rounded-2xl text-brand-primary"/>
                CacheFlow
            </h1>

            <div className="
                flex justify-around items-center p-2
                md:flex-col md:justify-start md:gap-2 md:p-0
            ">
                {menuItems.map((item, index) => {
                    const isActive = activeChild instanceof item.child;

                    return (
                        <button
                            key={index}
                            className={`${btnBase} ${isActive ? btnActive : btnInActive} p-2`}
                            onClick={() => {
                                component.onOutput(item.output)
                            }}
                        >
                            {isActive && (
                                <motion.div
                                    layoutId="nav-active-bg"
                                    className="hidden md:block absolute inset-0 bg-surface-nav-active rounded-2xl pointer-events-none"
                                    transition={{type: "spring", stiffness: 300, damping: 30}}
                                />
                            )}

                            <span className="relative z-10">{item.icon}</span>
                            <span className="relative z-10 text-[10px] md:text-base font-medium mt-1 md:mt-0">
                                {item.label}
                            </span>
                            {isActive && (
                                <motion.div
                                    layoutId="nav-dot"
                                    className="md:hidden absolute -bottom-1 w-1 h-1 bg-brand-primary rounded-full"
                                />
                            )}
                        </button>
                    );
                })}
            </div>
        </nav>
    );
}

export default NavBar;
