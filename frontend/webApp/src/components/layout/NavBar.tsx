import { RootChild, RootComponent, RootOutput } from "k2ts";
import { LuWallet } from "react-icons/lu";
import { FiHome } from "react-icons/fi";
import { GrLineChart } from "react-icons/gr";
import { LuSettings } from "react-icons/lu";
import { motion } from "framer-motion";

interface RootTabBarProps {
    component: RootComponent;
    activeChild: RootChild
}

const NavBar = ({component, activeChild}: RootTabBarProps) => {
    const btnBase = "relative flex flex-col md:flex-row items-center rounded-2xl text-base font-semibold gap-4 px-5 md:px-4 py-3 transition-colors duration-300 outline-none";
    const btnActive = "text-brand-indigo";
    const btnInActive = "text-slate-500 hover:text-slate-700";

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
    ];

    return (
        <nav className="
        z-50
        bottom-0 fixed w-full
        md:flex md:h-full md:w-96 md:flex-col md:relative md:p-6
        bg-sidebar-bg p-2 gap-8 border-r border-black/5
        ">
            <h1 className="
            hidden
            md:flex
            items-center gap-3 font-bold text-2xl">
                <LuWallet className="w-12 h-12 p-2.5 bg-sidebar-active rounded-2xl text-brand-indigo" />
                CashFlow
            </h1>

            <div className="
            flex
            md:flex md:flex-col
            gap-2
            justify-center
            ">
                {menuItems.map((item, index) => {
                    const isActive = activeChild instanceof item.child;

                    return (
                        <button
                            key={index}
                            className={`${btnBase} ${isActive ? btnActive : btnInActive}`}
                            onClick={() => { component.onOutput(item.output) }}
                        >
                            {isActive && (
                                <motion.div
                                    layoutId="nav-active-bg"
                                    className="
                                    pointer-events-none
                                    absolute inset-0 bg-sidebar-active rounded-2xl
                                    "
                                    transition={{ type: "spring", stiffness: 300, damping: 30 }}
                                />
                            )}

                            <span className="relative z-10">{item.icon}</span>
                            <span className="relative z-10 text-xs md:text-base w-18">{item.label}</span>
                        </button>
                    );
                })}
            </div>
        </nav>
    );
}

export default NavBar;