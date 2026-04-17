import {motion} from "framer-motion";
import {useId} from "react";
import {SettingsTabOption} from "../types.ts";

interface SegmentedTabsProps<T extends string> {
    options: SettingsTabOption<T>[];
    active: T;
    onChange: (value: T) => void;
}

const tabClass = "w-full h-10 rounded-xl text-base font-medium";

const SegmentedTabs = <T extends string>({options, active, onChange}: SegmentedTabsProps<T>) => {
    const layoutId = useId();

    return (
        <div className="rounded-xl bg-surface-muted p-1">
            <div className={`grid gap-1 ${options.length === 2 ? "grid-cols-2" : "grid-cols-1"}`}>
                {options.map((option) => (
                    <button
                        key={option.id}
                        className={`${tabClass} relative ${active === option.id ? "text-text-primary" : "text-text-nav"}`}
                        onClick={() => onChange(option.id)}
                        type="button"
                    >
                        {active === option.id && (
                            <motion.div
                                className="absolute inset-0 rounded-xl bg-surface-base shadow-sm"
                                layoutId={layoutId}
                                transition={{type: "spring", duration: 0.35}}
                            />
                        )}
                        <span className="relative z-10">{option.label}</span>
                    </button>
                ))}
            </div>
        </div>
    );
};

export default SegmentedTabs;
